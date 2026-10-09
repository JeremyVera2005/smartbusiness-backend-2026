package com.smartbusiness.usuario.service;

import com.smartbusiness.common.exception.AccesoNoAutorizadoException;
import com.smartbusiness.common.exception.CredencialesInvalidasException;
import com.smartbusiness.common.exception.DatoInvalidoException;
import com.smartbusiness.common.exception.ResourceNotFoundException;
import com.smartbusiness.common.exception.RecursoDuplicadoException;
import com.smartbusiness.config.JwtUtil;
import com.smartbusiness.usuario.dto.CambiarPasswordRequest;
import com.smartbusiness.usuario.dto.LoginRequest;
import com.smartbusiness.usuario.dto.LoginResponse;
import com.smartbusiness.usuario.dto.RegistroDuenoRequest;
import com.smartbusiness.usuario.model.Usuario;
import com.smartbusiness.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registro inicial del administrador/dueño de negocio (todavía sin
     * ningún negocio creado). Luego, desde "Crear negocio" queda como
     * DUEÑO de lo que cree (ver NegocioService.crearNegocio).
     */
    @Transactional
    public LoginResponse registrarDueno(RegistroDuenoRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        usuario = usuarioRepository.save(usuario);
        return generarLoginResponse(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        if (!usuario.getActivo()) {
            throw new AccesoNoAutorizadoException("La cuenta está deshabilitada");
        }

        return generarLoginResponse(usuario);
    }

    /**
     * Cambio de contraseña desde "Mi cuenta". Pide la actual para que una
     * sesión olvidada abierta no baste para tomar control de la cuenta.
     * La contraseña actual incorrecta es 400 (no 401) para que el frontend
     * no lo confunda con una sesión expirada.
     */
    @Transactional
    public void cambiarPassword(Long idUsuario, CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new DatoInvalidoException("La contraseña actual no es correcta");
        }
        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPasswordHash())) {
            throw new DatoInvalidoException("La nueva contraseña debe ser distinta de la actual");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    private LoginResponse generarLoginResponse(Usuario usuario) {
        String token = jwtUtil.generarToken(usuario.getIdUsuario(), usuario.getEmail());
        return LoginResponse.builder()
                .token(token)
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }
}
