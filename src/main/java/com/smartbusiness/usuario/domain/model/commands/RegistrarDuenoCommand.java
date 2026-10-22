package com.smartbusiness.usuario.domain.model.commands;

/** Command: registro inicial de un dueño (todavía sin negocio creado). */
public record RegistrarDuenoCommand(String nombre, String email, String password) {
}
