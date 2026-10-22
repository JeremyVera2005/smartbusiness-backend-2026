package com.smartbusiness.inventario.domain.model.commands;

import com.smartbusiness.inventario.domain.model.valueobjects.TipoMovimiento;

/** Command: registra una entrada, salida o ajuste y actualiza el stock del producto. */
public record RegistrarMovimientoCommand(
        Long idProducto,
        TipoMovimiento tipoMovimiento,
        Integer cantidad,
        String motivo
) {
}
