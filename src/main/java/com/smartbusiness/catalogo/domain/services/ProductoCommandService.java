package com.smartbusiness.catalogo.domain.services;

import com.smartbusiness.catalogo.domain.model.aggregates.Producto;
import com.smartbusiness.catalogo.domain.model.commands.ActualizarProductoCommand;
import com.smartbusiness.catalogo.domain.model.commands.CrearProductoCommand;
import com.smartbusiness.catalogo.domain.model.commands.EliminarProductoCommand;

/** Operaciones que modifican el estado del catálogo (lado Command de CQRS). */
public interface ProductoCommandService {

    Producto handle(CrearProductoCommand command);

    Producto handle(ActualizarProductoCommand command);

    void handle(EliminarProductoCommand command);
}
