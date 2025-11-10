package com.grupo1.ingsw_app.domain;

import java.util.UUID;

public class ObraSocial {

    private UUID id;
    private String nombre;

    public ObraSocial(UUID id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public String getNombre(){
        return this.nombre;
    }
}
