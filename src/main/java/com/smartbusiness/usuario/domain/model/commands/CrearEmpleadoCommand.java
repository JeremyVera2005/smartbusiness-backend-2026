package com.smartbusiness.usuario.domain.model.commands;

/** Command: el dueño da de alta a un trabajador en uno de sus negocios. */
public record CrearEmpleadoCommand(
        Long idNegocio,
        Long idUsuarioSolicitante,
        String nombre,
        String email,
        String password
) {
}
