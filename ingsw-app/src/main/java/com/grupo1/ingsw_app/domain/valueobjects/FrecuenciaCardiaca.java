package com.grupo1.ingsw_app.domain.valueobjects;

public class FrecuenciaCardiaca extends Frecuencia {

    public FrecuenciaCardiaca(Double v) {
        super(validar(v));
    }

    private static Double validar(Double v) {
        if (v == null || v.isNaN() || v.isInfinite())
            throw new IllegalArgumentException("La frecuencia cardíaca debe ser un número válido (latidos por minuto)");
        if (v < 0)
            throw new IllegalArgumentException("La frecuencia cardíaca no puede ser negativa");
        return v;
    }
}
