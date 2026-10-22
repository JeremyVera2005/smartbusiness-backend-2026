package com.smartbusiness.catalogo.interfaces.rest.transform;

import com.smartbusiness.catalogo.domain.model.commands.ActualizarProductoCommand;
import com.smartbusiness.catalogo.domain.model.commands.CrearProductoCommand;
import com.smartbusiness.catalogo.interfaces.rest.resources.ProductoRequest;

/** Convierte el body HTTP en Commands del dominio. */
public class ProductoCommandFromRequestAssembler {

    private ProductoCommandFromRequestAssembler() {
    }

    public static CrearProductoCommand toCrearCommand(ProductoRequest request) {
        return new CrearProductoCommand(
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecioVenta(),
                request.getPrecioCompra(),
                request.getStockActual(),
                request.getStockMinimo(),
                request.getCategoria(),
                request.getAtributosExtra());
    }

    public static ActualizarProductoCommand toActualizarCommand(Long idProducto, ProductoRequest request) {
        return new ActualizarProductoCommand(
                idProducto,
                request.getNombre(),
                request.getDescripcion(),
                request.getPrecioVenta(),
                request.getPrecioCompra(),
                request.getStockActual(),
                request.getStockMinimo(),
                request.getCategoria(),
                request.getAtributosExtra());
    }
}
