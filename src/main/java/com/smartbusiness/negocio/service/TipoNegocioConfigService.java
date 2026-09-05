package com.smartbusiness.negocio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.negocio.dto.TipoNegocioConfigResponse;
import com.smartbusiness.negocio.model.TipoNegocio;
import com.smartbusiness.negocio.model.TipoNegocioConfig;
import com.smartbusiness.negocio.repository.TipoNegocioConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoNegocioConfigService {

    @Autowired
    private TipoNegocioConfigRepository configRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Devuelve, ya parseada de JSON, la configuración que el frontend Angular
     * usa para armar el CRUD dinámico (campos extra y módulos activos) según
     * el tipo de negocio.
     */
    public TipoNegocioConfigResponse obtenerConfig(TipoNegocio tipoNegocio) {
        TipoNegocioConfig config = configRepository.findById(tipoNegocio)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe configuración para el tipo de negocio: " + tipoNegocio));

        try {
            Object camposExtra = objectMapper.readValue(config.getCamposProductoExtra(), Object.class);
            Object modulosActivos = objectMapper.readValue(config.getModulosActivos(), Object.class);

            return TipoNegocioConfigResponse.builder()
                    .tipoNegocio(tipoNegocio.name())
                    .camposProductoExtra(camposExtra)
                    .modulosActivos(modulosActivos)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear configuración de " + tipoNegocio, e);
        }
    }
}
