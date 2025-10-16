package com.grupo1.ingsw_app.domain.valueobjects;

public class Temperatura {
    private float temperatura;

    public Temperatura(Float valor) {
        if (valor == null || valor.isNaN() || valor.isInfinite()||valor < 0)
            throw new IllegalArgumentException("La temperatura debe ser un número válido en grados Celsius");
        // Si tu regla de negocio prohíbe < 0, se valida acá:
        this.temperatura = valor;
    }

    public float getTemperatura() { return temperatura; }

}
