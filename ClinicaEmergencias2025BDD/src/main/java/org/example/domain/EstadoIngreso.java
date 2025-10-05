package org.example.domain;

public enum EstadoIngreso {
    PENDIENTE ("PENDIENTE"),
    EN_PROCESO ("EN_PROCESO"),
    FINALIZADO ("FINALIZADO");

    String nombre;
    EstadoIngreso(String nombre){
        this.nombre=nombre;
    }
}

