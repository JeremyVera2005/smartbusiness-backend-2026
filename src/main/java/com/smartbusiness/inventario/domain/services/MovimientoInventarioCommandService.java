package com.smartbusiness.inventario.domain.services;

import com.smartbusiness.inventario.domain.model.aggregates.MovimientoInventario;
import com.smartbusiness.inventario.domain.model.commands.RegistrarMovimientoCommand;

/** Operaciones que modifican el inventario (lado Command de CQRS). */
public interface MovimientoInventarioCommandService {

    MovimientoInventario handle(RegistrarMovimientoCommand command);
}
