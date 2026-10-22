package com.smartbusiness.usuario.application.internal.commandservices;

import com.smartbusiness.negocio.domain.model.aggregates.UsuarioNegocio;
import com.smartbusiness.negocio.domain.model.valueobjects.RolNegocio;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.NegocioRepository;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.UsuarioNegocioRepository;
import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.ReglaNegocioException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.usuario.domain.model.aggregates.Usuario;
import com.smartbusiness.usuario.domain.model.commands.CrearEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.commands.QuitarEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.commands.RestablecerPasswordEmpleadoCommand;
import com.smartbusiness.usuario.domain.model.readmodels.EmpleadoAsignado;
import com.smartbusiness.usuario.domain.services.EmpleadoCommandService;
import com.smartbusiness.usuario.infrastructure.persistence.jpa.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regla de negocio central de este módulo: SOLO el usuario con rol DUEÑO
 * en un negocio puede dar de alta personal para ese negocio. Un EMPLEADO
 * nunca tiene acceso a estas operaciones (no puede crear otros usuarios),
 * y el nuevo trabajador queda asignado únicamente al negocio indicado.
 */
@Service
public class EmpleadoCommandServiceImpl implements EmpleadoCommandService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioNegocioRepository usuarioNegocioRepository;
    private final NegocioRepository negocioRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoCommandServiceImpl(UsuarioRepository usuarioRepository,
                                      UsuarioNegocioRepository usuarioNegocioRepository,
                                      NegocioRepository negocioRepository,
                                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioNegocioRepository = usuarioNegocioRepository;
        this.negocioRepository = negocioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public EmpleadoAsignado handle(CrearEmpleadoCommand command) {
        Long idNegocio = command.idNegocio();
        validarEsDueno(idNegocio, command.idUsuarioSolicitante());

        if (!negocioRepository.existsById(idNegocio)) {
            throw new ResourceNotFoundException("Negocio no encontrado: " + idNegocio);
        }

        Usuario nuevoUsuario;
        boolean yaExiste = usuarioRepository.existsByEmail(command.email());

        if (yaExiste) {
            // La persona ya tiene cuenta (ej. trabaja en otro negocio del mismo dueño);
            // simplemente se le asigna también a este negocio como EMPLEADO.
            nuevoUsuario = usuarioRepository.findByEmail(command.email())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        } else {
            nuevoUsuario = Usuario.builder()
                    .nombre(command.nombre())
                    .email(command.email())
                    .passwordHash(passwordEncoder.encode(command.password()))
                    .build();
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);
        }

        UsuarioNegocio.UsuarioNegocioId relacionId =
                new UsuarioNegocio.UsuarioNegocioId(nuevoUsuario.getIdUsuario(), idNegocio);

        if (usuarioNegocioRepository.existsById(relacionId)) {
            throw new ReglaNegocioException("Ese usuario ya está asignado a este negocio");
        }

        UsuarioNegocio relacion = UsuarioNegocio.builder()
                .id(relacionId)
                .rol(RolNegocio.EMPLEADO)
                .build();
        usuarioNegocioRepository.save(relacion);

        return new EmpleadoAsignado(nuevoUsuario, RolNegocio.EMPLEADO, idNegocio);
    }

    @Override
    @Transactional
    public void handle(QuitarEmpleadoCommand command) {
        Long idNegocio = command.idNegocio();
        validarEsDueno(idNegocio, command.idUsuarioSolicitante());

        UsuarioNegocio.UsuarioNegocioId relacionId =
                new UsuarioNegocio.UsuarioNegocioId(command.idUsuarioEmpleado(), idNegocio);

        UsuarioNegocio relacion = usuarioNegocioRepository.findById(relacionId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no pertenece a este negocio"));

        if (relacion.getRol() == RolNegocio.DUENO) {
            throw new AccesoNoAutorizadoException("No se puede eliminar al dueño del negocio");
        }

        usuarioNegocioRepository.delete(relacion);
    }

    /**
     * El dueño asigna una contraseña temporal a un empleado que olvidó la
     * suya. No aplica a quien es dueño de algún negocio: esa persona gestiona
     * su propia cuenta y otro dueño no debe poder tomar control de ella.
     */
    @Override
    @Transactional
    public void handle(RestablecerPasswordEmpleadoCommand command) {
        Long idNegocio = command.idNegocio();
        Long idUsuarioEmpleado = command.idUsuarioEmpleado();
        validarEsDueno(idNegocio, command.idUsuarioSolicitante());

        UsuarioNegocio relacion = usuarioNegocioRepository
                .findByIdIdUsuarioAndIdIdNegocio(idUsuarioEmpleado, idNegocio)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no pertenece a este negocio"));

        boolean esDuenoEnAlgunNegocio = usuarioNegocioRepository.findByIdIdUsuario(idUsuarioEmpleado).stream()
                .anyMatch(rel -> rel.getRol() == RolNegocio.DUENO);
        if (relacion.getRol() != RolNegocio.EMPLEADO || esDuenoEnAlgunNegocio) {
            throw new AccesoNoAutorizadoException(
                    "Solo puedes restablecer la contraseña de empleados que no son dueños de ningún negocio");
        }

        Usuario usuario = usuarioRepository.findById(idUsuarioEmpleado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setPasswordHash(passwordEncoder.encode(command.nuevaPassword()));
        usuarioRepository.save(usuario);
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
