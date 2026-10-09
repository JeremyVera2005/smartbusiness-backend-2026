package com.smartbusiness.config;

import com.smartbusiness.negocio.model.RolNegocio;

/**
 * Guarda en un ThreadLocal el id_negocio "activo" de la petición actual y el
 * rol que el usuario autenticado tiene en ese negocio. Así los Service pueden
 * filtrar por negocio y validar permisos sin recibirlos como parámetro.
 */
public class TenantContext {

    private static final ThreadLocal<Long> NEGOCIO_ACTUAL = new ThreadLocal<>();
    private static final ThreadLocal<RolNegocio> ROL_ACTUAL = new ThreadLocal<>();

    public static void setNegocioActual(Long idNegocio, RolNegocio rol) {
        NEGOCIO_ACTUAL.set(idNegocio);
        ROL_ACTUAL.set(rol);
    }

    public static Long getNegocioActual() {
        return NEGOCIO_ACTUAL.get();
    }

    public static RolNegocio getRolActual() {
        return ROL_ACTUAL.get();
    }

    public static void clear() {
        NEGOCIO_ACTUAL.remove();
        ROL_ACTUAL.remove();
    }
}
