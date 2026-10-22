package com.smartbusiness.usuario.domain.model.commands;

/** Command: el dueño asigna una contraseña temporal a un empleado. */
public record RestablecerPasswordEmpleadoCommand(
        Long idNegocio,
        Long idUsuarioEmpleado,
        Long idUsuarioSolicitante,
        String nuevaPassword
) {
}
