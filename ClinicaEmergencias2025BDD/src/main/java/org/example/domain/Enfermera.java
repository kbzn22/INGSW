package org.example.domain;

public class Enfermera {
    private String nombre;
    private String apellido;

    public Enfermera(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }
    public String getNombre() { return this.nombre; }
    public String getApellido() { return this.apellido; }
}