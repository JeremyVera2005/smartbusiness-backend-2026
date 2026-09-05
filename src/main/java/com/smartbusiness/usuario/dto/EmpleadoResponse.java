package com.smartbusiness.usuario.dto;

import com.smartbusiness.negocio.model.RolNegocio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoResponse {
    private Long idUsuario;
    private String nombre;
    private String email;
    private RolNegocio rol;
    private Long idNegocio;
}
