package com.smartbusiness.catalogo.domain.model.commands;

import java.math.BigDecimal;
import java.util.Map;

/** Command: registra un producto nuevo en el catálogo del negocio activo. */
public record CrearProductoCommand(
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
