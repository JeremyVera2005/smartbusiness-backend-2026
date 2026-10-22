package com.smartbusiness.usuario.domain.services;

import com.smartbusiness.usuario.domain.model.queries.IniciarSesionQuery;
import com.smartbusiness.usuario.domain.model.readmodels.SesionUsuario;

/** Consultas de autenticación (lado Query de CQRS). */
public interface AuthQueryService {

    SesionUsuario handle(IniciarSesionQuery query);
}
