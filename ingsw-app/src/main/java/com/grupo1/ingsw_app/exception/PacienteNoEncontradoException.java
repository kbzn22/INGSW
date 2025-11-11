package com.grupo1.ingsw_app.exception;

public class PacienteNoEncontradoException extends RuntimeException {
    public PacienteNoEncontradoException(String cuil) {
        super("No se encontró un paciente con CUIL: " + cuil);
    }

}
