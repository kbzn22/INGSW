package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public class Enfermera extends Persona{
    String matricula;
    Usuario usuario;

    public Enfermera(String cuil, String nombre, String apellido, String matricula, String email,Usuario usuario) {
        super(cuil, nombre, apellido, email);
        this.matricula = matricula;
        this.usuario=usuario;
    }
    public Enfermera(String cuil, String nombre, String apellido, String matricula, String email) {
        super(cuil, nombre, apellido, email);
        this.matricula = matricula;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getMatricula() {
        return matricula;
    }
}
