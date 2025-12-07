package com.grupo1.ingsw_app.exception;

public class PacienteRedundanteEnColaException extends RuntimeException {
    public PacienteRedundanteEnColaException(String cuil) {
        super("El paciente " + cuil + " ya se encuentra en la Cola de atencion.");
    }
}
