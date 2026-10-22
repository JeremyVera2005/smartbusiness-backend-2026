package com.smartbusiness.usuario.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarPasswordRequest {

    @NotBlank(message = "Ingresa tu contraseña actual")
    private String passwordActual;

    @NotBlank(message = "Ingresa la nueva contraseña")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String passwordNueva;
}
