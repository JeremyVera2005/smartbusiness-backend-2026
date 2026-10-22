package com.smartbusiness.negocio.domain.services;

import com.smartbusiness.negocio.domain.model.aggregates.Negocio;
import com.smartbusiness.negocio.domain.model.commands.CrearNegocioCommand;

/** Operaciones que modifican los negocios (lado Command de CQRS). */
public interface NegocioCommandService {

    Negocio handle(CrearNegocioCommand command);
}
