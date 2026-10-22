package com.smartbusiness.negocio.interfaces.rest.transform;

import com.smartbusiness.negocio.domain.model.aggregates.TipoNegocioConfig;
import com.smartbusiness.negocio.interfaces.rest.resources.TipoNegocioConfigResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Devuelve, ya parseada de JSON, la configuración que el frontend Angular
 * usa para armar el CRUD dinámico (campos extra y módulos activos).
 */
@Component
public class TipoNegocioConfigResponseFromEntityAssembler {

    private final ObjectMapper objectMapper;

    public TipoNegocioConfigResponseFromEntityAssembler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TipoNegocioConfigResponse toResponse(TipoNegocioConfig config) {
        try {
            Object camposExtra = objectMapper.readValue(config.getCamposProductoExtra(), Object.class);
            Object modulosActivos = objectMapper.readValue(config.getModulosActivos(), Object.class);

            return TipoNegocioConfigResponse.builder()
                    .tipoNegocio(config.getTipoNegocio().name())
                    .camposProductoExtra(camposExtra)
                    .modulosActivos(modulosActivos)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear configuración de " + config.getTipoNegocio(), e);
        }
    }
}
