package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public class Paciente extends Persona {

    private Domicilio domicilio;
    private Afiliado afiliado;

    public Paciente (){
    }

    public Paciente(String cuil, String nombre) {
        super(new Cuil(cuil), nombre);
    }

    public Paciente(String cuil, String nombre, String apellido, String email, String calle, String numero, String localidad, ObraSocial obraSocial, String numeroAfiliado) {
        super(new Cuil(cuil), nombre, apellido, email);
        this.domicilio = new Domicilio(calle, numero, localidad);

        this.afiliado = new Afiliado(obraSocial, numeroAfiliado);
    }

}