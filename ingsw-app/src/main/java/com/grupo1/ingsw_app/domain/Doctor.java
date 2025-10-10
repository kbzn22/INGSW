package com.grupo1.ingsw_app.domain;

public class Doctor extends Persona{
    private String matricula;

    public Doctor(String nombre, String apellido, String cuil, String email, String matricula) {
        super(nombre, apellido, cuil, email);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
