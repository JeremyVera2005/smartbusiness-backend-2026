package com.smartbusiness.common.exception;

/** Operación válida en forma pero que una regla del negocio impide (p. ej. stock insuficiente). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String message) {
        super(message);
    }
}
