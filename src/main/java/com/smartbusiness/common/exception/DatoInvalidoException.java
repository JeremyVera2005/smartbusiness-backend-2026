package com.smartbusiness.common.exception;

/** Dato enviado por el usuario que no es válido para la operación (HTTP 400). */
public class DatoInvalidoException extends RuntimeException {
    public DatoInvalidoException(String message) {
        super(message);
    }
}
