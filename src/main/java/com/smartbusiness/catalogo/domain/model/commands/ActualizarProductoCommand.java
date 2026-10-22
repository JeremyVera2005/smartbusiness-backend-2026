package com.smartbusiness.catalogo.domain.model.commands;

import java.math.BigDecimal;
import java.util.Map;

/** Command: modifica los datos de un producto existente. */
public record ActualizarProductoCommand(
        Long idProducto,
        String nombre,
        String descripcion,
        BigDecimal precioVenta,
        BigDecimal precioCompra,
        Integer stockActual,
        Integer stockMinimo,
        String categoria,
        Map<String, Object> atributosExtra
) {
}
