package com.smartbusiness.usuario.service;

import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.negocio.model.RolNegocio;
import com.smartbusiness.negocio.model.UsuarioNegocio;
import com.smartbusiness.negocio.repository.NegocioRepository;
import com.smartbusiness.negocio.repository.UsuarioNegocioRepository;
import com.smartbusiness.usuario.dto.CrearEmpleadoRequest;
import com.smartbusiness.usuario.dto.EmpleadoResponse;
import com.smartbusiness.usuario.model.Usuario;
import com.smartbusiness.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Regla de negocio central de este módulo: SOLO el usuario con rol DUEÑO
 * en un negocio puede dar de alta personal para ese negocio. Un EMPLEADO
 * nunca tiene acceso a estos endpoints (no puede crear otros usuarios),
 * y el nuevo trabajador queda asignado únicamente al negocio indicado.
 */
@Service
public class EmpleadoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioNegocioRepository usuarioNegocioRepository;

    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public EmpleadoResponse crearEmpleado(Long idNegocio, Long idUsuarioSolicitante, CrearEmpleadoRequest request) {
        validarEsDueno(idNegocio, idUsuarioSolicitante);

        if (!negocioRepository.existsById(idNegocio)) {
            throw new ResourceNotFoundException("Negocio no encontrado: " + idNegocio);
        }

        Usuario nuevoUsuario;
        boolean yaExiste = usuarioRepository.existsByEmail(request.getEmail());

        if (yaExiste) {
            // La persona ya tiene cuenta (ej. trabaja en otro negocio del mismo dueño);
            // simplemente se le asigna también a este negocio como EMPLEADO.
            nuevoUsuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        } else {
            nuevoUsuario = Usuario.builder()
                    .nombre(request.getNombre())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .build();
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);
        }

        UsuarioNegocio.UsuarioNegocioId relacionId =
                new UsuarioNegocio.UsuarioNegocioId(nuevoUsuario.getIdUsuario(), idNegocio);

        if (usuarioNegocioRepository.existsById(relacionId)) {
            throw new IllegalStateException("Ese usuario ya está asignado a este negocio");
        }

        UsuarioNegocio relacion = UsuarioNegocio.builder()
                .id(relacionId)
                .rol(RolNegocio.EMPLEADO)
                .build();
        usuarioNegocioRepository.save(relacion);

        return EmpleadoResponse.builder()
                .idUsuario(nuevoUsuario.getIdUsuario())
                .nombre(nuevoUsuario.getNombre())
                .email(nuevoUsuario.getEmail())
                .rol(RolNegocio.EMPLEADO)
                .idNegocio(idNegocio)
                .build();
    }

    public List<EmpleadoResponse> listarEmpleados(Long idNegocio, Long idUsuarioSolicitante) {
        validarEsDueno(idNegocio, idUsuarioSolicitante);

        return usuarioNegocioRepository.findByIdIdNegocio(idNegocio).stream()
                .map(rel -> {
                    Usuario usuario = usuarioRepository.findById(rel.getId().getIdUsuario())
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                    return EmpleadoResponse.builder()
                            .idUsuario(usuario.getIdUsuario())
                            .nombre(usuario.getNombre())
                            .email(usuario.getEmail())
                            .rol(rel.getRol())
                            .idNegocio(idNegocio)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void quitarEmpleado(Long idNegocio, Long idUsuarioEmpleado, Long idUsuarioSolicitante) {
        validarEsDueno(idNegocio, idUsuarioSolicitante);

        UsuarioNegocio.UsuarioNegocioId relacionId =
                new UsuarioNegocio.UsuarioNegocioId(idUsuarioEmpleado, idNegocio);

        UsuarioNegocio relacion = usuarioNegocioRepository.findById(relacionId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no pertenece a este negocio"));

        if (relacion.getRol() == RolNegocio.DUENO) {
            throw new AccesoNoAutorizadoException("No se puede eliminar al dueño del negocio");
        }

        usuarioNegocioRepository.delete(relacion);
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
