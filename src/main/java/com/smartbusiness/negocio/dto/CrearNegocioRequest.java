package com.smartbusiness.negocio.dto;

import com.smartbusiness.negocio.model.TamanoNegocio;
import com.smartbusiness.negocio.model.TipoNegocio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CrearNegocioRequest {

    @NotBlank(message = "El nombre del negocio es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de negocio es obligatorio")
    private TipoNegocio tipoNegocio;

    private String ubicacion;

    private TamanoNegocio tamano;

    private LocalTime horarioApertura;

    private LocalTime horarioCierre;
}
