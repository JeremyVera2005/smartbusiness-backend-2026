package com.smartbusiness.negocio.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Se envía ya "parseado" al frontend Angular para que arme el CRUD dinámico
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoNegocioConfigResponse {
    private String tipoNegocio;
    private Object camposProductoExtra; // lista de campos dinámicos
    private Object modulosActivos;      // mapa de módulos habilitados
}
