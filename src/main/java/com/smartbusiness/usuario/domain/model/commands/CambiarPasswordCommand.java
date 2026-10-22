package com.smartbusiness.usuario.domain.model.commands;

/** Command: el usuario autenticado cambia su propia contraseña. */
public record CambiarPasswordCommand(Long idUsuario, String passwordActual, String passwordNueva) {
}
