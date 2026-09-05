package com.smartbusiness.ia.service;

import com.smartbusiness.catalogo.model.Producto;
import com.smartbusiness.catalogo.repository.ProductoRepository;
import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.config.TenantContext;
import com.smartbusiness.ia.dto.AlertaStock;
import com.smartbusiness.ventas.model.DetalleVenta;
import com.smartbusiness.ventas.repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fase A del motor de IA: reglas y estadística simple sobre datos reales
 * (sin librerías de ML). Calcula riesgo de quiebre de stock a partir del
 * promedio de venta diario de los últimos N días.
 */
@Service
public class RecomendacionService {

    private static final int DIAS_HISTORIAL = 14;
    private static final double UMBRAL_CRITICO_DIAS = 2.0;
    private static final double UMBRAL_ALERTA_DIAS = 5.0;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    public List<AlertaStock> detectarRiesgoQuiebre() {
        Long idNegocio = obtenerNegocioActivo();
        LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_HISTORIAL);

        List<Producto> productos = productoRepository.findByIdNegocioAndActivoTrue(idNegocio);

        return productos.stream()
                .map(producto -> calcularAlerta(idNegocio, producto, desde))
                .filter(alerta -> alerta.getPromedioVentaDiario() > 0
                        && alerta.getDiasRestantesEstimados() < UMBRAL_ALERTA_DIAS)
                .collect(Collectors.toList());
    }

    private AlertaStock calcularAlerta(Long idNegocio, Producto producto, LocalDateTime desde) {
        List<DetalleVenta> ventasRecientes =
                detalleVentaRepository.findPorProductoDesde(idNegocio, producto.getIdProducto(), desde);

        int totalVendido = ventasRecientes.stream().mapToInt(DetalleVenta::getCantidad).sum();
        double promedioDiario = totalVendido / (double) DIAS_HISTORIAL;

        double diasRestantes = promedioDiario > 0
                ? producto.getStockActual() / promedioDiario
                : Double.MAX_VALUE;

        String nivel = diasRestantes < UMBRAL_CRITICO_DIAS ? "CRITICO" : "ALERTA";

        return AlertaStock.builder()
                .idProducto(producto.getIdProducto())
                .nombreProducto(producto.getNombre())
                .stockActual(producto.getStockActual())
                .promedioVentaDiario(promedioDiario)
                .diasRestantesEstimados(diasRestantes)
                .nivel(nivel)
                .build();
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
