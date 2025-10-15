package com.grupo1.ingsw_app.domain.valueobjects;

public class TensionArterial {

    private final Frecuencia frecuenciaSistolica;
    private final Frecuencia frecuenciaDiastolica;


    public TensionArterial(Double sistolica, Double diastolica) {
        if (sistolica == null || diastolica == null)
            throw new IllegalArgumentException("La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
        if (sistolica < 0 || diastolica < 0)
            throw new IllegalArgumentException("La presión arterial no puede ser negativa");

        this.frecuenciaSistolica = new Frecuencia(sistolica);
        this.frecuenciaDiastolica = new Frecuencia(diastolica);
    }

    public Frecuencia getSistolica() { return frecuenciaSistolica; }
    public Frecuencia getDiastolica() { return frecuenciaDiastolica; }
}
