package com.grupo1.ingsw_app.domain;

public class Enfermera extends Persona{
    String matricula;

    public Enfermera(String nombre, String apellido, String cuil, String email, String matricula) {
        super(nombre, apellido, cuil, email);
        this.matricula = matricula;
    }
}
