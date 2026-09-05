package com.smartbusiness.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepta cada request autenticada, lee el header X-Id-Negocio
 * (que el frontend Angular manda en cada llamada una vez que el usuario
 * eligió un negocio en "Mis negocios") y lo deja disponible en TenantContext.
 *
 * En el futuro, si prefieren no confiar en un header, esto puede leer
 * el id_negocio directamente de un claim dentro del JWT.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    public static final String HEADER_ID_NEGOCIO = "X-Id-Negocio";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String idNegocioHeader = request.getHeader(HEADER_ID_NEGOCIO);
        if (idNegocioHeader != null && !idNegocioHeader.isBlank()) {
            TenantContext.setNegocioActual(Long.parseLong(idNegocioHeader));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
