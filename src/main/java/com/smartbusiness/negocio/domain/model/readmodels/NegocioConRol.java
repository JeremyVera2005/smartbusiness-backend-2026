package com.smartbusiness.negocio.domain.model.readmodels;

import com.smartbusiness.negocio.domain.model.aggregates.Negocio;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;

/** Resultado de lectura: un negocio junto con el rol del usuario que consulta. */
public record NegocioConRol(Negocio negocio, RolNegocio rol) {
}
