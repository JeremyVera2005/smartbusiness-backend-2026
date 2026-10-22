package com.smartbusiness.negocio.interfaces.rest.transform;

import com.smartbusiness.negocio.domain.model.aggregates.Negocio;
import com.smartbusiness.negocio.domain.model.readmodels.NegocioConRol;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.negocio.interfaces.rest.resources.NegocioResponse;

public class NegocioResponseFromEntityAssembler {

    private NegocioResponseFromEntityAssembler() {
    }

    public static NegocioResponse toResponse(NegocioConRol negocioConRol) {
        return toResponse(negocioConRol.negocio(), negocioConRol.rol());
    }

    public static NegocioResponse toResponse(Negocio negocio, RolNegocio rolUsuario) {
        return NegocioResponse.builder()
                .idNegocio(negocio.getIdNegocio())
                .nombre(negocio.getNombre())
                .tipoNegocio(negocio.getTipoNegocio())
                .ubicacion(negocio.getUbicacion())
                .tamano(negocio.getTamano())
                .horarioApertura(negocio.getHorarioApertura())
                .horarioCierre(negocio.getHorarioCierre())
                .fechaCreacion(negocio.getFechaCreacion())
                .rolUsuario(rolUsuario)
                .build();
    }
}
