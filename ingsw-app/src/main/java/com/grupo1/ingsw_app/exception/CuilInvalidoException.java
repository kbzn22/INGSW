package com.grupo1.ingsw_app.exception;

public class CuilInvalidoException extends RuntimeException {
    public CuilInvalidoException(String valor) {
        super("El CUIL ingresado es inválido: " + valor);
    }
}
