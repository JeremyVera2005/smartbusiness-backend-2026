package com.smartbusiness.negocio.service;

import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.negocio.dto.CrearNegocioRequest;
import com.smartbusiness.negocio.dto.NegocioResponse;
import com.smartbusiness.negocio.model.*;
import com.smartbusiness.negocio.repository.NegocioRepository;
import com.smartbusiness.negocio.repository.UsuarioNegocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private UsuarioNegocioRepository usuarioNegocioRepository;

    @Transactional
    public NegocioResponse crearNegocio(Long idUsuario, CrearNegocioRequest request) {
        Negocio negocio = Negocio.builder()
                .idUsuarioPropietario(idUsuario)
                .nombre(request.getNombre())
                .tipoNegocio(request.getTipoNegocio())
                .ubicacion(request.getUbicacion())
                .tamano(request.getTamano())
                .horarioApertura(request.getHorarioApertura())
                .horarioCierre(request.getHorarioCierre())
                .build();

        negocio = negocioRepository.save(negocio);

        UsuarioNegocio.UsuarioNegocioId relacionId =
                new UsuarioNegocio.UsuarioNegocioId(idUsuario, negocio.getIdNegocio());

        UsuarioNegocio relacion = UsuarioNegocio.builder()
                .id(relacionId)
                .rol(RolNegocio.DUENO)
                .build();

        usuarioNegocioRepository.save(relacion);

        return mapearAResponse(negocio, RolNegocio.DUENO);
    }

    public List<NegocioResponse> listarMisNegocios(Long idUsuario) {
        List<UsuarioNegocio> relaciones = usuarioNegocioRepository.findByIdIdUsuario(idUsuario);

        return relaciones.stream()
                .map(rel -> {
                    Negocio negocio = negocioRepository.findById(rel.getId().getIdNegocio())
                            .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado"));
                    return mapearAResponse(negocio, rel.getRol());
                })
                .collect(Collectors.toList());
    }

    public NegocioResponse obtenerPorId(Long idNegocio, Long idUsuario) {
        UsuarioNegocio relacion = usuarioNegocioRepository
                .findByIdIdUsuarioAndIdIdNegocio(idUsuario, idNegocio)
                .orElseThrow(() -> new AccesoNoAutorizadoException(
                        "El usuario no tiene acceso al negocio " + idNegocio));

        Negocio negocio = negocioRepository.findById(idNegocio)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado: " + idNegocio));
        return mapearAResponse(negocio, relacion.getRol());
    }

    /**
     * Valida que el usuario autenticado realmente tenga acceso al negocio
     * que está pidiendo. Se usa antes de cualquier operación sensible.
     */
    public void validarAccesoUsuario(Long idNegocio, Long idUsuario) {
        boolean tieneAcceso = usuarioNegocioRepository.findByIdIdUsuario(idUsuario).stream()
                .anyMatch(rel -> rel.getId().getIdNegocio().equals(idNegocio));

        if (!tieneAcceso) {
            throw new AccesoNoAutorizadoException(
                    "El usuario no tiene acceso al negocio " + idNegocio);
        }
    }

    private NegocioResponse mapearAResponse(Negocio negocio, RolNegocio rolUsuario) {
        return NegocioResponse.builder()
                .idNegocio(negocio.getIdNegocio())
                .nombre(negocio.getNombre())
                .tipoNegocio(negocio.getTipoNegocio())
                .ubicacion(negocio.getUbicacion())
                .tamano(negocio.getTamano())
                .horarioApertura(negocio.getHorarioApertura())
                .horarioCierre(negocio.getHorarioCierre())
                .fechaCreacion(negocio.getFechaCreacion())
                .rolUsuario(rolUsuario)
                .build();
    }
}
