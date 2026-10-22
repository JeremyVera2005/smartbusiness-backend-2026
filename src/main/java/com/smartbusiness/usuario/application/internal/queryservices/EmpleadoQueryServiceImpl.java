package com.smartbusiness.usuario.application.internal.queryservices;

import com.smartbusiness.negocio.domain.model.aggregates.UsuarioNegocio;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.UsuarioNegocioRepository;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.usuario.domain.model.aggregates.Usuario;
import com.smartbusiness.usuario.domain.model.queries.ObtenerEmpleadosQuery;
import com.smartbusiness.usuario.domain.model.readmodels.EmpleadoAsignado;
import com.smartbusiness.usuario.domain.services.EmpleadoQueryService;
import com.smartbusiness.usuario.infrastructure.persistence.jpa.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmpleadoQueryServiceImpl implements EmpleadoQueryService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioNegocioRepository usuarioNegocioRepository;

    public EmpleadoQueryServiceImpl(UsuarioRepository usuarioRepository,
                                    UsuarioNegocioRepository usuarioNegocioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioNegocioRepository = usuarioNegocioRepository;
    }

    @Override
    public List<EmpleadoAsignado> handle(ObtenerEmpleadosQuery query) {
        Long idNegocio = query.idNegocio();
        validarEsDueno(idNegocio, query.idUsuarioSolicitante());

        return usuarioNegocioRepository.findByIdIdNegocio(idNegocio).stream()
                .map(rel -> {
                    Usuario usuario = usuarioRepository.findById(rel.getId().getIdUsuario())
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                    return new EmpleadoAsignado(usuario, rel.getRol(), idNegocio);
                })
                .collect(Collectors.toList());
    }

    private void validarEsDueno(Long idNegocio, Long idUsuarioSolicitante) {
        UsuarioNegocio relacion = usuarioNegocioRepository
                .findByIdIdUsuarioAndIdIdNegocio(idUsuarioSolicitante, idNegocio)
                .orElseThrow(() -> new AccesoNoAutorizadoException(
                        "El usuario no tiene relación con este negocio"));

        if (relacion.getRol() != RolNegocio.DUENO) {
            throw new AccesoNoAutorizadoException(
                    "Solo el dueño del negocio puede gestionar personal");
        }
    }
}
