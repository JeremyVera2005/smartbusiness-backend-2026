package com.smartbusiness.usuario.domain.model.readmodels;

import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.usuario.domain.model.aggregates.Usuario;

/** Resultado de lectura: un usuario y el rol que tiene en un negocio. */
public record EmpleadoAsignado(Usuario usuario, RolNegocio rol, Long idNegocio) {
}
