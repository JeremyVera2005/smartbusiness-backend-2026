package com.smartbusiness.negocio.dto;

import com.smartbusiness.negocio.model.RolNegocio;
import com.smartbusiness.negocio.model.TamanoNegocio;
import com.smartbusiness.negocio.model.TipoNegocio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NegocioResponse {
    private Long idNegocio;
    private String nombre;
    private TipoNegocio tipoNegocio;
    private String ubicacion;
    private TamanoNegocio tamano;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private LocalDateTime fechaCreacion;

    // Rol del usuario autenticado dentro de este negocio (para UI basada en rol en Angular)
    private RolNegocio rolUsuario;
}
