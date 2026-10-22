package com.smartbusiness.negocio.domain.model.commands;

import com.smartbusiness.negocio.domain.model.valueobjects.TamanoNegocio;
import com.smartbusiness.negocio.domain.model.valueobjects.TipoNegocio;

import java.time.LocalTime;

/** Command: crea un negocio y deja al usuario que lo crea como DUEÑO. */
public record CrearNegocioCommand(
        Long idUsuario,
        String nombre,
        TipoNegocio tipoNegocio,
        String ubicacion,
        TamanoNegocio tamano,
        LocalTime horarioApertura,
        LocalTime horarioCierre
) {
}
