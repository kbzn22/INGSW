package com.grupo1.ingsw_app.exception;

public class EntidadNoEncontradaException extends RuntimeException {
    public EntidadNoEncontradaException(String entidad, String campo) {
        super("No se encontró '" + entidad + "' con "+ campo);
    }

}
