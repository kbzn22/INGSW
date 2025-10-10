package com.grupo1.ingsw_app.domain;

public class Prioridad {
    int nivel;
    String color;
    String descripcion;


    public Prioridad( int nivel,String color, String descripcion) {
        this.color = color;
        this.nivel = nivel;
        this.descripcion = descripcion;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
