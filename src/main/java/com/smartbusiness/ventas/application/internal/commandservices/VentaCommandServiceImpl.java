package com.smartbusiness.ventas.application.internal.commandservices;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.infrastructure.persistence.jpa.repositories.ProductoRepository;
import com.smartbusiness.inventario.domain.model.commands.RegistrarMovimientoCommand;
import com.smartbusiness.inventario.domain.model.valueobjects.TipoMovimiento;
import com.smartbusiness.inventario.domain.services.MovimientoInventarioCommandService;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ReglaNegocioException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import com.smartbusiness.ventas.domain.model.aggregates.Venta;
import com.smartbusiness.ventas.domain.model.commands.RegistrarVentaCommand;
import com.smartbusiness.ventas.domain.model.entities.DetalleVenta;
import com.smartbusiness.ventas.domain.services.VentaCommandService;
import com.smartbusiness.ventas.infrastructure.persistence.jpa.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaCommandServiceImpl implements VentaCommandService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioCommandService movimientoInventarioCommandService;

    public VentaCommandServiceImpl(VentaRepository ventaRepository,
                                   ProductoRepository productoRepository,
                                   MovimientoInventarioCommandService movimientoInventarioCommandService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.movimientoInventarioCommandService = movimientoInventarioCommandService;
    }

    /**
     * Registra la venta completa: valida stock, calcula totales, descuenta
     * inventario (vía el Command de inventario, que ya deja el movimiento
     * auditado) y persiste todo en una sola transacción.
     */
    @Override
    @Transactional
    public Venta handle(RegistrarVentaCommand command) {
        Long idNegocio = obtenerNegocioActivo();

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        Venta venta = Venta.builder()
                .idNegocio(idNegocio)
                .total(BigDecimal.ZERO)
                .build();

        for (RegistrarVentaCommand.Detalle item : command.detalles()) {
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado: " + item.idProducto()));

            if (!producto.getIdNegocio().equals(idNegocio)) {
                throw new AccesoNoAutorizadoException("El producto no pertenece al negocio activo");
            }

            if (producto.getStockActual() < item.cantidad()) {
                throw new ReglaNegocioException(
                        "Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(item.cantidad()));

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .idProducto(producto.getIdProducto())
                    .cantidad(item.cantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .subtotal(subtotal)
                    .build();

            detalles.add(detalle);
            total = total.add(subtotal);

            // Descuenta stock y deja auditoría del movimiento
            movimientoInventarioCommandService.handle(new RegistrarMovimientoCommand(
                    producto.getIdProducto(), TipoMovimiento.SALIDA, item.cantidad(), "Venta"));
        }

        venta.setTotal(total);
        venta.setDetalles(detalles);

        return ventaRepository.save(venta);
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
