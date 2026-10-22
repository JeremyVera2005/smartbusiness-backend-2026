package com.smartbusiness.negocio.interfaces.rest.transform;

import com.smartbusiness.negocio.domain.model.commands.CrearNegocioCommand;
import com.smartbusiness.negocio.interfaces.rest.resources.CrearNegocioRequest;

public class CrearNegocioCommandFromRequestAssembler {

    private CrearNegocioCommandFromRequestAssembler() {
    }

    public static CrearNegocioCommand toCommand(Long idUsuario, CrearNegocioRequest request) {
        return new CrearNegocioCommand(
                idUsuario,
                request.getNombre(),
                request.getTipoNegocio(),
                request.getUbicacion(),
                request.getTamano(),
                request.getHorarioApertura(),
                request.getHorarioCierre());
    }
}
