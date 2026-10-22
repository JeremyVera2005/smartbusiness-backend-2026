package com.smartbusiness.ventas.domain.services;

import com.smartbusiness.ventas.domain.model.aggregates.Venta;
import com.smartbusiness.ventas.domain.model.commands.RegistrarVentaCommand;

/** Operaciones que modifican las ventas (lado Command de CQRS). */
public interface VentaCommandService {

    Venta handle(RegistrarVentaCommand command);
}
