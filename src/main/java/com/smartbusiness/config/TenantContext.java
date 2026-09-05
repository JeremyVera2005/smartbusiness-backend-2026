package com.smartbusiness.config;

/**
 * Guarda en un ThreadLocal el id_negocio "activo" de la petición actual.
 * Así los Service pueden filtrar por negocio sin recibirlo como parámetro
 * en cada método, y sin riesgo de que un desarrollador se olvide del filtro.
 */
public class TenantContext {

    private static final ThreadLocal<Long> NEGOCIO_ACTUAL = new ThreadLocal<>();

    public static void setNegocioActual(Long idNegocio) {
        NEGOCIO_ACTUAL.set(idNegocio);
    }

    public static Long getNegocioActual() {
        return NEGOCIO_ACTUAL.get();
    }

    public static void clear() {
        NEGOCIO_ACTUAL.remove();
    }
}
