package org.example.domain;

public class Paciente {
    private String Cuil;
    private String nombre;
    private String apellido;
    private String obraSocial;

    public Paciente(String Cuil, String nombre, String apellido, String obraSocial) {
        this.Cuil = Cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.obraSocial = obraSocial;
    }

    public String getCuil() {
        return Cuil;
    }
}
