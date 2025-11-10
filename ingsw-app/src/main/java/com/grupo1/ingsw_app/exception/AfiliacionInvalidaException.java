package com.grupo1.ingsw_app.exception;

import java.util.UUID;

public class AfiliacionInvalidaException extends RuntimeException {
    public AfiliacionInvalidaException(UUID idObraSocial, String numeroAfiliado) {
        super("El numero de afiliado " + numeroAfiliado + " no pertenece a la obra social: " + idObraSocial);
    }
}
