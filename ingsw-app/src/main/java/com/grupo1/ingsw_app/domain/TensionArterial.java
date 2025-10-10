package com.grupo1.ingsw_app.domain;

public class TensionArterial {

    private final Frecuencia frecuenciaSistolica;
    private final Frecuencia frecuenciaDiastolica;


    public TensionArterial(Frecuencia frecuenciaSistolica, Frecuencia frecuenciaDiastolica) {
        this.frecuenciaSistolica = frecuenciaSistolica;
        this.frecuenciaDiastolica = frecuenciaDiastolica;
    }

    public Frecuencia getFrecuenciaSistolica() {
        return frecuenciaSistolica;
    }

    public Frecuencia getFrecuenciaDiastolica() {
        return frecuenciaDiastolica;
    }
}
