package com.smartbusiness.usuario.domain.services;

import com.smartbusiness.usuario.domain.model.queries.ObtenerEmpleadosQuery;
import com.smartbusiness.usuario.domain.model.readmodels.EmpleadoAsignado;

import java.util.List;

/** Consultas sobre el personal (lado Query de CQRS). */
public interface EmpleadoQueryService {

    List<EmpleadoAsignado> handle(ObtenerEmpleadosQuery query);
}
