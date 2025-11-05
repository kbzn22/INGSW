package com.grupo1.ingsw_app.exception;

public class CampoInvalidoException extends RuntimeException {
    public CampoInvalidoException(String campo, String detalle) {
        super("El campo '" + campo + "' es inválido: " + detalle);
    }
}
