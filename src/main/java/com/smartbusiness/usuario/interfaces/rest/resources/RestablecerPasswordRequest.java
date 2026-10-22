package com.smartbusiness.usuario.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestablecerPasswordRequest {

    @NotBlank(message = "Ingresa la nueva contraseña temporal")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
