package com.smartbusiness.ia.application.internal.queryservices;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.infrastructure.persistence.jpa.repositories.ProductoRepository;
import com.smartbusiness.ia.domain.model.queries.ObtenerAlertasDemandaQuery;
import com.smartbusiness.ia.domain.model.valueobjects.AlertaDemanda;
import com.smartbusiness.ia.domain.services.DeteccionAnomaliaQueryService;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import com.smartbusiness.ventas.domain.model.entities.DetalleVenta;
import com.smartbusiness.ventas.infrastructure.persistence.jpa.repositories.DetalleVentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fase B del motor de IA: compara la ventana de los últimos 7 días contra
 * los 7 días anteriores por producto, y marca variaciones fuertes de
 * demanda (sin modelos entrenados, solo agregación + resta porcentual).
 */
@Service
@Transactional(readOnly = true)
public class DeteccionAnomaliaQueryServiceImpl implements DeteccionAnomaliaQueryService {

    private static final int TAMANO_VENTANA_DIAS = 7;
    private static final double UMBRAL_VARIACION_PORCENTUAL = 30.0;

    private final ProductoRepository productoRepository;

    private final DetalleVentaRepository detalleVentaRepository;

    public DeteccionAnomaliaQueryServiceImpl(ProductoRepository productoRepository,
            DetalleVentaRepository detalleVentaRepository) {
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Override
    public List<AlertaDemanda> handle(ObtenerAlertasDemandaQuery query) {
        Long idNegocio = obtenerNegocioActivo();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioVentanaActual = ahora.minusDays(TAMANO_VENTANA_DIAS);
        LocalDateTime inicioVentanaAnterior = ahora.minusDays(TAMANO_VENTANA_DIAS * 2L);

        List<Producto> productos = productoRepository.findByIdNegocioAndActivoTrue(idNegocio);

        return productos.stream()
                .map(p -> calcularVariacion(idNegocio, p, inicioVentanaActual, inicioVentanaAnterior))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<AlertaDemanda> calcularVariacion(Long idNegocio, Producto producto,
                                                        LocalDateTime inicioActual, LocalDateTime inicioAnterior) {
        int unidadesActual = sumarCantidad(
                detalleVentaRepository.findPorProductoDesde(idNegocio, producto.getIdProducto(), inicioActual));

        List<DetalleVenta> ventasVentanaCompleta =
                detalleVentaRepository.findPorProductoDesde(idNegocio, producto.getIdProducto(), inicioAnterior);

        int unidadesAnterior = sumarCantidad(ventasVentanaCompleta) - unidadesActual;

        if (unidadesAnterior == 0) {
            return Optional.empty(); // sin base de comparación confiable todavía
        }

        double variacion = ((unidadesActual - unidadesAnterior) / (double) unidadesAnterior) * 100.0;

        if (Math.abs(variacion) < UMBRAL_VARIACION_PORCENTUAL) {
            return Optional.empty();
        }

        return Optional.of(AlertaDemanda.builder()
                .idProducto(producto.getIdProducto())
                .nombreProducto(producto.getNombre())
                .unidadesSemanaActual(unidadesActual)
                .unidadesSemanaAnterior(unidadesAnterior)
                .variacionPorcentual(variacion)
                .tipo(variacion > 0 ? "ALTA_DEMANDA" : "CAIDA_VENTAS")
                .build());
    }

    private int sumarCantidad(List<DetalleVenta> detalles) {
        return detalles.stream().mapToInt(DetalleVenta::getCantidad).sum();
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
