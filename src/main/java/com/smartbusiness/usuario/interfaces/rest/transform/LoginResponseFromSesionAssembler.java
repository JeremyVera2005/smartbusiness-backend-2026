package com.smartbusiness.usuario.interfaces.rest.transform;

import com.smartbusiness.usuario.domain.model.readmodels.SesionUsuario;
import com.smartbusiness.usuario.interfaces.rest.resources.LoginResponse;

public class LoginResponseFromSesionAssembler {

    private LoginResponseFromSesionAssembler() {
    }

    public static LoginResponse toResponse(SesionUsuario sesion) {
        return LoginResponse.builder()
                .token(sesion.token())
                .idUsuario(sesion.usuario().getIdUsuario())
                .nombre(sesion.usuario().getNombre())
                .email(sesion.usuario().getEmail())
                .build();
    }
}
