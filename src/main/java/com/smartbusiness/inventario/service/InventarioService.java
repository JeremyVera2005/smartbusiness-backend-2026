package com.smartbusiness.inventario.service;

import com.smartbusiness.common.exception.ReglaNegocioException;
import com.smartbusiness.catalogo.model.Producto;
import com.smartbusiness.catalogo.repository.ProductoRepository;
import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.config.TenantContext;
import com.smartbusiness.inventario.dto.MovimientoRequest;
import com.smartbusiness.inventario.dto.MovimientoResponse;
import com.smartbusiness.inventario.model.MovimientoInventario;
import com.smartbusiness.inventario.model.TipoMovimiento;
import com.smartbusiness.inventario.repository.MovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Registra un movimiento de inventario y actualiza el stock del producto
     * en la misma transacción, para que nunca queden desincronizados.
     */
    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest request) {
        Long idNegocio = obtenerNegocioActivo();

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (!producto.getIdNegocio().equals(idNegocio)) {
            throw new AccesoNoAutorizadoException("El producto no pertenece al negocio activo");
        }

        if (request.getTipoMovimiento() == TipoMovimiento.SALIDA
                && request.getCantidad() > producto.getStockActual()) {
            throw new ReglaNegocioException("Stock insuficiente para \"" + producto.getNombre()
                    + "\": hay " + producto.getStockActual() + " y se intentó retirar " + request.getCantidad() + ".");
        }

        int nuevoStock = calcularNuevoStock(producto.getStockActual(), request.getTipoMovimiento(), request.getCantidad());
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .idNegocio(idNegocio)
                .idProducto(producto.getIdProducto())
                .tipoMovimiento(request.getTipoMovimiento())
                .cantidad(request.getCantidad())
                .stockResultante(nuevoStock)
                .motivo(request.getMotivo())
                .build();

        movimiento = movimientoRepository.save(movimiento);
        return mapearAResponse(movimiento);
    }

    public List<MovimientoResponse> historialPorProducto(Long idProducto) {
        Long idNegocio = obtenerNegocioActivo();
        return movimientoRepository
                .findByIdNegocioAndIdProductoOrderByFechaMovimientoDesc(idNegocio, idProducto)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
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

    private MovimientoResponse mapearAResponse(MovimientoInventario m) {
        return MovimientoResponse.builder()
                .idMovimiento(m.getIdMovimiento())
                .idProducto(m.getIdProducto())
                .tipoMovimiento(m.getTipoMovimiento())
                .cantidad(m.getCantidad())
                .stockResultante(m.getStockResultante())
                .motivo(m.getMotivo())
                .fechaMovimiento(m.getFechaMovimiento())
                .build();
    }
}
