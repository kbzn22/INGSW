package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public class Paciente extends Persona {

    private Domicilio domicilio;
    private Afiliado obraSocial;

    public Paciente (){
    }

    public Paciente(String cuil, String nombre) {
        super(new Cuil(cuil), nombre);
    }

    public Paciente(String cuil, String nombre, String apellido, String email, String calle, String numero, String localidad) {
        super(new Cuil(cuil), nombre, apellido, email);
        this.domicilio = new Domicilio(calle, numero, localidad);
    }

}