package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

public class Domicilio {

    private String calle;
    private Integer numero;
    private String localidad;

    public Domicilio(String calle, Integer numero, String localidad) {
        // Validaciones básicas de integridad
        if (calle == null || calle.trim().isEmpty())
            throw new CampoInvalidoException("numero", "no puede estar vacía");

        if (numero == null || numero <= 0)
            throw new CampoInvalidoException("numero", "debe ser un número positivo");

        if (localidad == null || localidad.trim().isEmpty())
            throw new CampoInvalidoException("localidad", "no puede estar vacía");

        this.calle = calle.trim();
        this.numero = numero;
        this.localidad = localidad.trim();
    }
    public String getCalle() {
        return calle;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

}
