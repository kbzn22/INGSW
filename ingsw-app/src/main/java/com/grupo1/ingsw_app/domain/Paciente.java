package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

public class Paciente extends Persona {

    public Paciente (){
    }

    public Paciente(String cuil, String nombre) {
        super(new Cuil(cuil), nombre);
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