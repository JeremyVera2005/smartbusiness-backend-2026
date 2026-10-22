package com.smartbusiness.usuario.interfaces.rest.transform;

import com.smartbusiness.usuario.domain.model.readmodels.EmpleadoAsignado;
import com.smartbusiness.usuario.interfaces.rest.resources.EmpleadoResponse;

public class EmpleadoResponseFromReadModelAssembler {

    private EmpleadoResponseFromReadModelAssembler() {
    }

    public static EmpleadoResponse toResponse(EmpleadoAsignado empleado) {
        return EmpleadoResponse.builder()
                .idUsuario(empleado.usuario().getIdUsuario())
                .nombre(empleado.usuario().getNombre())
                .email(empleado.usuario().getEmail())
                .rol(empleado.rol())
                .idNegocio(empleado.idNegocio())
                .build();
    }
}
