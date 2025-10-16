package com.grupo1.ingsw_app.domain.valueobjects;

public class FrecuenciaRespiratoria extends Frecuencia {

    public FrecuenciaRespiratoria(Double v) {
        super(validar(v));
    }

    private static Double validar(Double v) {
        if (v == null || v.isNaN() || v.isInfinite()||v < 0)
            throw new IllegalArgumentException("La frecuencia respiratoria debe ser un número válido (respiraciones por minuto)");
        return v;
    }
}
