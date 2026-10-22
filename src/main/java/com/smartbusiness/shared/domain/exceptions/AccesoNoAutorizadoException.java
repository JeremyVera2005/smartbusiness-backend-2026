package com.smartbusiness.shared.domain.exceptions;

public class AccesoNoAutorizadoException extends RuntimeException {
    public AccesoNoAutorizadoException(String message) {
        super(message);
    }
}
