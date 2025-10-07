package com.grupo1.ingsw_app.domain;

public class Paciente {
    private String dni;
    private String nombre;

    // para usar el dataTable necesitas crear constructor vacio noseporq
    public Paciente() {}

    public Paciente(String dni, String nombre) {
        this.dni = dni;
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }
    /*

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente)) return false;
        Paciente p = (Paciente) o;
        return Objects.equals(dni, p.dni)
                && Objects.equals(nombre, p.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni, nombre);
    }*/

}