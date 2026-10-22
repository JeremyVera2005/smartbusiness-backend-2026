package com.smartbusiness.usuario.interfaces.rest.resources;

import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
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
