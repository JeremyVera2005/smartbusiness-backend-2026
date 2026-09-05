package com.smartbusiness.usuario.service;

import com.smartbusiness.config.JwtUtil;
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
            throw new IllegalStateException("Ya existe una cuenta con ese email");
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
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new IllegalStateException("La cuenta está deshabilitada");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return generarLoginResponse(usuario);
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
