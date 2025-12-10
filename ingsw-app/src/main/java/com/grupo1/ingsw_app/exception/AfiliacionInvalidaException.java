package com.grupo1.ingsw_app.exception;

import java.util.UUID;

public class AfiliacionInvalidaException extends RuntimeException {
    public AfiliacionInvalidaException(String obraSocial, String numeroAfiliado) {
        super("El numero de afiliado " + numeroAfiliado + " no pertenece a la obra social: " + obraSocial);
    }
}
