package com.grupo1.ingsw_app.domain;

public abstract class Persona {
    private String nombre;
    private String apellido;
    private String cuil;
    private String email;

    public Persona(String nombre, String apellido, String cuil, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cuil = cuil;
        this.email = email;
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

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
