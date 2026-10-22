package com.smartbusiness.negocio.application.internal.queryservices;

import com.smartbusiness.negocio.domain.model.aggregates.Negocio;
import com.smartbusiness.negocio.domain.model.aggregates.TipoNegocioConfig;
import com.smartbusiness.negocio.domain.model.aggregates.UsuarioNegocio;
import com.smartbusiness.negocio.domain.model.queries.ObtenerConfigTipoNegocioQuery;
import com.smartbusiness.negocio.domain.model.queries.ObtenerMisNegociosQuery;
import com.smartbusiness.negocio.domain.model.queries.ObtenerNegocioPorIdQuery;
import com.smartbusiness.negocio.domain.model.readmodels.NegocioConRol;
import com.smartbusiness.negocio.domain.services.NegocioQueryService;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.NegocioRepository;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.TipoNegocioConfigRepository;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.UsuarioNegocioRepository;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NegocioQueryServiceImpl implements NegocioQueryService {

    private final NegocioRepository negocioRepository;
    private final UsuarioNegocioRepository usuarioNegocioRepository;
    private final TipoNegocioConfigRepository configRepository;

    public NegocioQueryServiceImpl(NegocioRepository negocioRepository,
                                   UsuarioNegocioRepository usuarioNegocioRepository,
                                   TipoNegocioConfigRepository configRepository) {
        this.negocioRepository = negocioRepository;
        this.usuarioNegocioRepository = usuarioNegocioRepository;
        this.configRepository = configRepository;
    }

    @Override
    public List<NegocioConRol> handle(ObtenerMisNegociosQuery query) {
        List<UsuarioNegocio> relaciones = usuarioNegocioRepository.findByIdIdUsuario(query.idUsuario());

        return relaciones.stream()
                .map(rel -> {
                    Negocio negocio = negocioRepository.findById(rel.getId().getIdNegocio())
                            .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado"));
                    return new NegocioConRol(negocio, rel.getRol());
                })
                .collect(Collectors.toList());
    }

    @Override
    public NegocioConRol handle(ObtenerNegocioPorIdQuery query) {
        Long idNegocio = query.idNegocio();
        UsuarioNegocio relacion = usuarioNegocioRepository
                .findByIdIdUsuarioAndIdIdNegocio(query.idUsuario(), idNegocio)
                .orElseThrow(() -> new AccesoNoAutorizadoException(
                        "El usuario no tiene acceso al negocio " + idNegocio));

        Negocio negocio = negocioRepository.findById(idNegocio)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado: " + idNegocio));
        return new NegocioConRol(negocio, relacion.getRol());
    }

    @Override
    public TipoNegocioConfig handle(ObtenerConfigTipoNegocioQuery query) {
        return configRepository.findById(query.tipoNegocio())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe configuración para el tipo de negocio: " + query.tipoNegocio()));
    }
}
