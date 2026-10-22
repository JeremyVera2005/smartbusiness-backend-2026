package com.smartbusiness.catalogo.domain.model.commands;

/** Command: desactiva un producto (borrado lógico). */
public record EliminarProductoCommand(Long idProducto) {
}
