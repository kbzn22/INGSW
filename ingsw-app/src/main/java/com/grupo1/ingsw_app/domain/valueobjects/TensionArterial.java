package com.grupo1.ingsw_app.domain.valueobjects;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

public class TensionArterial {

    private final Frecuencia frecuenciaSistolica;
    private final Frecuencia frecuenciaDiastolica;


    public TensionArterial(Double sistolica, Double diastolica) {
        if (sistolica == null || diastolica == null||sistolica < 0 || diastolica < 0)
            throw new CampoInvalidoException("tensionArterial",
                    "debe tener valores positivos válidos para las frecuencias sistólica y diastólica (milímetros de mercurio)");
        this.frecuenciaSistolica = new Frecuencia(sistolica);
        this.frecuenciaDiastolica = new Frecuencia(diastolica);
    }

    public Frecuencia getSistolica() {
        return frecuenciaSistolica;
    }

    public Frecuencia getDiastolica() {
        return frecuenciaDiastolica;
    }
}
