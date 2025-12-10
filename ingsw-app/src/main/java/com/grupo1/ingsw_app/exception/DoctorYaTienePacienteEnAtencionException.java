package com.grupo1.ingsw_app.exception;

public class DoctorYaTienePacienteEnAtencionException extends RuntimeException {
    public DoctorYaTienePacienteEnAtencionException() {
        super("El Doctor ya tiene un paciente en atencion.");
    }
}
