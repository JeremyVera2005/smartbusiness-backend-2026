package com.smartbusiness.usuario.domain.model.readmodels;

import com.smartbusiness.usuario.domain.model.aggregates.Usuario;

/** Resultado de autenticación: el usuario y el JWT generado para él. */
public record SesionUsuario(String token, Usuario usuario) {
}
