package com.grupo1.ingsw_app.exception;

public class AfiliadoUtilizadoException extends RuntimeException {
    public AfiliadoUtilizadoException(String numeroAfiliado) {
        super("El numero de afiliado " + numeroAfiliado + " ya esta registrado en el sistema");
    }
}