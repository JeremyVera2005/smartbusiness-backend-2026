package com.smartbusiness.shared.infrastructure.tenant;

import com.smartbusiness.negocio.domain.model.aggregates.UsuarioNegocio;
import com.smartbusiness.negocio.infrastructure.persistence.jpa.repositories.UsuarioNegocioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

/**
 * Intercepta cada request autenticada, lee el header X-Id-Negocio
 * (que el frontend Angular manda en cada llamada una vez que el usuario
 * eligió un negocio en "Mis negocios") y lo deja disponible en TenantContext.
 *
 * Antes de aceptarlo verifica que el usuario autenticado pertenezca a ese
 * negocio: si no, responde 403 y la petición no llega al controller. Así un
 * usuario no puede leer ni modificar datos de otro negocio cambiando el header.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    public static final String HEADER_ID_NEGOCIO = "X-Id-Negocio";

    private final UsuarioNegocioRepository usuarioNegocioRepository;

    public TenantInterceptor(UsuarioNegocioRepository usuarioNegocioRepository) {
        this.usuarioNegocioRepository = usuarioNegocioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String idNegocioHeader = request.getHeader(HEADER_ID_NEGOCIO);
        if (idNegocioHeader == null || idNegocioHeader.isBlank()) {
            return true;
        }

        Long idNegocio;
        try {
            idNegocio = Long.parseLong(idNegocioHeader.trim());
        } catch (NumberFormatException e) {
            return rechazar(response, HttpStatus.BAD_REQUEST, "El negocio indicado no es válido");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long idUsuario)) {
            // Rutas públicas (login, registro): el header no aplica
            return true;
        }

        Optional<UsuarioNegocio> relacion =
                usuarioNegocioRepository.findByIdIdUsuarioAndIdIdNegocio(idUsuario, idNegocio);
        if (relacion.isEmpty()) {
            return rechazar(response, HttpStatus.FORBIDDEN, "No perteneces a este negocio");
        }

        TenantContext.setNegocioActual(idNegocio, relacion.get().getRol());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }

    private boolean rechazar(HttpServletResponse response, HttpStatus status, String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status.value() + ",\"mensaje\":\"" + mensaje + "\"}");
        return false;
    }
}
