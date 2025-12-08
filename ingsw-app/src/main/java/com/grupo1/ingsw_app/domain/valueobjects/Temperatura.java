package com.grupo1.ingsw_app.domain.valueobjects;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

public class Temperatura {
    private Double temperatura;

    public Temperatura(Double valor) {
        if (valor == null || valor.isNaN() || valor.isInfinite() || valor < 0)
            throw new CampoInvalidoException("temperatura",
                    "debe tener valores positivos válidos (grados Celsius)");
        this.temperatura = valor;
    }

    public Double getTemperatura() { return temperatura; }

}
