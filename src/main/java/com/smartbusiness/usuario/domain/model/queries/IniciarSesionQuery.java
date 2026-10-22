package com.smartbusiness.usuario.domain.model.queries;

/** Query: valida credenciales y genera el token (no modifica datos). */
public record IniciarSesionQuery(String email, String password) {
}
