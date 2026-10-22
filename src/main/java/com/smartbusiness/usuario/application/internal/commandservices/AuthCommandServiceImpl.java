package com.smartbusiness.usuario.application.internal.commandservices;

import com.smartbusiness.shared.domain.exceptions.DatoInvalidoException;
import com.smartbusiness.shared.domain.exceptions.RecursoDuplicadoException;
import com.smartbusiness.shared.domain.exceptions.ResourceNotFoundException;
import com.smartbusiness.shared.infrastructure.security.JwtUtil;
import com.smartbusiness.usuario.domain.model.aggregates.Usuario;
import com.smartbusiness.usuario.domain.model.commands.CambiarPasswordCommand;
import com.smartbusiness.usuario.domain.model.commands.RegistrarDuenoCommand;
import com.smartbusiness.usuario.domain.model.readmodels.SesionUsuario;
import com.smartbusiness.usuario.domain.services.AuthCommandService;
import com.smartbusiness.usuario.infrastructure.persistence.jpa.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthCommandServiceImpl(UsuarioRepository usuarioRepository,
                                  PasswordEncoder passwordEncoder,
                                  JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registro inicial del administrador/dueño de negocio (todavía sin
     * ningún negocio creado). Luego, desde "Crear negocio" queda como
     * DUEÑO de lo que cree (ver NegocioCommandServiceImpl).
     */
    @Override
    @Transactional
    public SesionUsuario handle(RegistrarDuenoCommand command) {
        if (usuarioRepository.existsByEmail(command.email())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(command.nombre())
                .email(command.email())
                .passwordHash(passwordEncoder.encode(command.password()))
                .build();

        usuario = usuarioRepository.save(usuario);
        String token = jwtUtil.generarToken(usuario.getIdUsuario(), usuario.getEmail());
        return new SesionUsuario(token, usuario);
    }

    /**
     * Cambio de contraseña desde "Mi cuenta". Pide la actual para que una
     * sesión olvidada abierta no baste para tomar control de la cuenta.
     * La contraseña actual incorrecta es 400 (no 401) para que el frontend
     * no lo confunda con una sesión expirada.
     */
    @Override
    @Transactional
    public void handle(CambiarPasswordCommand command) {
        Usuario usuario = usuarioRepository.findById(command.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(command.passwordActual(), usuario.getPasswordHash())) {
            throw new DatoInvalidoException("La contraseña actual no es correcta");
        }
        if (passwordEncoder.matches(command.passwordNueva(), usuario.getPasswordHash())) {
            throw new DatoInvalidoException("La nueva contraseña debe ser distinta de la actual");
        }

        usuario.setPasswordHash(passwordEncoder.encode(command.passwordNueva()));
        usuarioRepository.save(usuario);
    }
}
