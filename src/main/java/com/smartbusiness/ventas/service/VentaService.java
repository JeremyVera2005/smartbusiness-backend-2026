package com.smartbusiness.ventas.service;

import com.smartbusiness.catalogo.model.Producto;
import com.smartbusiness.catalogo.repository.ProductoRepository;
import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.config.TenantContext;
import com.smartbusiness.inventario.dto.MovimientoRequest;
import com.smartbusiness.inventario.model.TipoMovimiento;
import com.smartbusiness.inventario.service.InventarioService;
import com.smartbusiness.ventas.dto.DetalleVentaRequest;
import com.smartbusiness.ventas.dto.VentaRequest;
import com.smartbusiness.ventas.dto.VentaResponse;
import com.smartbusiness.ventas.model.DetalleVenta;
import com.smartbusiness.ventas.model.Venta;
import com.smartbusiness.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioService inventarioService;

    /**
     * Registra la venta completa: valida stock, calcula totales, descuenta
     * inventario (vía InventarioService, que ya deja el movimiento auditado)
     * y persiste todo en una sola transacción.
     */
    @Transactional
    public VentaResponse registrarVenta(VentaRequest request) {
        Long idNegocio = obtenerNegocioActivo();

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        Venta venta = Venta.builder()
                .idNegocio(idNegocio)
                .total(BigDecimal.ZERO)
                .build();

        for (DetalleVentaRequest detalleRequest : request.getDetalles()) {
            Producto producto = productoRepository.findById(detalleRequest.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado: " + detalleRequest.getIdProducto()));

            if (!producto.getIdNegocio().equals(idNegocio)) {
                throw new AccesoNoAutorizadoException("El producto no pertenece al negocio activo");
            }

            if (producto.getStockActual() < detalleRequest.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .idProducto(producto.getIdProducto())
                    .cantidad(detalleRequest.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .subtotal(subtotal)
                    .build();

            detalles.add(detalle);
            total = total.add(subtotal);

            // Descuenta stock y deja auditoría del movimiento
            MovimientoRequest movimiento = new MovimientoRequest();
            movimiento.setIdProducto(producto.getIdProducto());
            movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
            movimiento.setCantidad(detalleRequest.getCantidad());
            movimiento.setMotivo("Venta");
            inventarioService.registrarMovimiento(movimiento);
        }

        venta.setTotal(total);
        venta.setDetalles(detalles);

        venta = ventaRepository.save(venta);
        return mapearAResponse(venta);
    }

    public List<VentaResponse> listar() {
        Long idNegocio = obtenerNegocioActivo();
        return ventaRepository.findByIdNegocioOrderByFechaVentaDesc(idNegocio).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }

    private VentaResponse mapearAResponse(Venta venta) {
        List<VentaResponse.DetalleVentaResponse> detalles = venta.getDetalles().stream()
                .map(d -> VentaResponse.DetalleVentaResponse.builder()
                        .idProducto(d.getIdProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return VentaResponse.builder()
                .idVenta(venta.getIdVenta())
                .total(venta.getTotal())
                .fechaVenta(venta.getFechaVenta())
                .detalles(detalles)
                .build();
    }
}
