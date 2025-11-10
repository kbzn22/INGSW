package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public abstract class Persona {
    private Cuil cuil;
    private String nombre;
    private String apellido;
    private String email;

    public Persona(){

    }

    public Persona(String cuil, String nombre, String apellido, String email) {
        this.cuil = new Cuil(cuil);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public Persona(Cuil cuil, String nombre) {
        this.cuil = cuil;
        this.nombre = nombre;
    }

    public Cuil getCuil() {
        return cuil;
    }

    public void setCuil(Cuil cuil) {
        this.cuil = cuil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
