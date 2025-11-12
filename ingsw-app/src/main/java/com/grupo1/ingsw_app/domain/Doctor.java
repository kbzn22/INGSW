package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public class Doctor extends Persona{
    private String matricula;
    private Usuario usuario;

    public Doctor(String nombre, String apellido, String cuil, String email, String matricula) {
        super(cuil,nombre, apellido, email);
        this.matricula = matricula;
    }
    public Doctor(String nombre, String apellido, String cuil, String email, String matricula, Usuario usuario) {
        super(cuil,nombre, apellido, email);
        this.matricula = matricula;
        this.usuario=usuario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
