package com.grupo1.ingsw_app.domain;

public class Domicilio {

    private String calle;
    private String numero;
    private String localidad;

    public Domicilio(String calle, String numero, String localidad){
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
    }
}
