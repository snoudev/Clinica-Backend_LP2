package com.clinica.clinica_backend.exception;

public class SolicitudInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
