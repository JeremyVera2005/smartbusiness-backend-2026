package com.smartbusiness.usuario.application.internal.queryservices;

import com.smartbusiness.shared.domain.exceptions.AccesoNoAutorizadoException;
import com.smartbusiness.shared.domain.exceptions.CredencialesInvalidasException;
import com.smartbusiness.shared.infrastructure.security.JwtUtil;
import com.smartbusiness.usuario.domain.model.aggregates.Usuario;
import com.smartbusiness.usuario.domain.model.queries.IniciarSesionQuery;
import com.smartbusiness.usuario.domain.model.readmodels.SesionUsuario;
import com.smartbusiness.usuario.domain.services.AuthQueryService;
import com.smartbusiness.usuario.infrastructure.persistence.jpa.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthQueryServiceImpl implements AuthQueryService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthQueryServiceImpl(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public SesionUsuario handle(IniciarSesionQuery query) {
        Usuario usuario = usuarioRepository.findByEmail(query.email())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        if (!passwordEncoder.matches(query.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        if (!usuario.getActivo()) {
            throw new AccesoNoAutorizadoException("La cuenta está deshabilitada");
        }

        String token = jwtUtil.generarToken(usuario.getIdUsuario(), usuario.getEmail());
        return new SesionUsuario(token, usuario);
    }
}
