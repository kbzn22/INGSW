package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public class Enfermera extends Persona{
    String matricula;

    public Enfermera(String cuil, String nombre, String apellido, String matricula, String email) {
        super(cuil, nombre, apellido, email);
        this.matricula = matricula;
    }
}
