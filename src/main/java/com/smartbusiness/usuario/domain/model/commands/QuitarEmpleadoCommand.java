package com.smartbusiness.usuario.domain.model.commands;

/** Command: el dueño retira a un empleado del negocio. */
public record QuitarEmpleadoCommand(Long idNegocio, Long idUsuarioEmpleado, Long idUsuarioSolicitante) {
}
