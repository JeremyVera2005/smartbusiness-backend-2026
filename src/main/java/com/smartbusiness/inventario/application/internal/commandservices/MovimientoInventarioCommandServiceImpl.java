package com.smartbusiness.inventario.application.internal.commandservices;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.infrastructure.persistence.jpa.repositories.ProductoRepository;
import com.smartbusiness.inventario.domain.model.aggregates.MovimientoInventario;
import com.smartbusiness.inventario.domain.model.commands.RegistrarMovimientoCommand;
import com.smartbusiness.inventario.domain.model.valueobjects.TipoMovimiento;
import com.smartbusiness.inventario.domain.services.MovimientoInventarioCommandService;
import com.smartbusiness.inventario.infrastructure.persistence.jpa.repositories.MovimientoInventarioRepository;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ReglaNegocioException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoInventarioCommandServiceImpl implements MovimientoInventarioCommandService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public MovimientoInventarioCommandServiceImpl(MovimientoInventarioRepository movimientoRepository,
                                                  ProductoRepository productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Registra un movimiento de inventario y actualiza el stock del producto
     * en la misma transacción, para que nunca queden desincronizados.
     */
    @Override
    @Transactional
    public MovimientoInventario handle(RegistrarMovimientoCommand command) {
        Long idNegocio = obtenerNegocioActivo();

        Producto producto = productoRepository.findById(command.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (!producto.getIdNegocio().equals(idNegocio)) {
            throw new AccesoNoAutorizadoException("El producto no pertenece al negocio activo");
        }

        if (command.tipoMovimiento() == TipoMovimiento.SALIDA
                && command.cantidad() > producto.getStockActual()) {
            throw new ReglaNegocioException("Stock insuficiente para \"" + producto.getNombre()
                    + "\": hay " + producto.getStockActual() + " y se intentó retirar " + command.cantidad() + ".");
        }

        int nuevoStock = calcularNuevoStock(producto.getStockActual(), command.tipoMovimiento(), command.cantidad());
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .idNegocio(idNegocio)
                .idProducto(producto.getIdProducto())
                .tipoMovimiento(command.tipoMovimiento())
                .cantidad(command.cantidad())
                .stockResultante(nuevoStock)
                .motivo(command.motivo())
                .build();

        return movimientoRepository.save(movimiento);
    }

    private int calcularNuevoStock(int stockActual, TipoMovimiento tipo, int cantidad) {
        return switch (tipo) {
            case ENTRADA -> stockActual + cantidad;
            case SALIDA -> Math.max(0, stockActual - cantidad);
            case AJUSTE -> cantidad; // ajuste fija el stock directamente
        };
    }

    private Long obtenerNegocioActivo() {
        Long idNegocio = TenantContext.getNegocioActual();
        if (idNegocio == null) {
            throw new AccesoNoAutorizadoException("No se especificó un negocio activo (header X-Id-Negocio)");
        }
        return idNegocio;
    }
}
