package com.smartbusiness.ventas.domain.model.commands;

import java.util.List;

/** Command: registra una venta, descuenta stock y deja el movimiento de inventario. */
public record RegistrarVentaCommand(List<Detalle> detalles) {

    public record Detalle(Long idProducto, Integer cantidad) {
    }
}
