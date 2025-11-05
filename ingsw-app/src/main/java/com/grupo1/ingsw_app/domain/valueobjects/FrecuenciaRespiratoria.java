package com.grupo1.ingsw_app.domain.valueobjects;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

public class FrecuenciaRespiratoria extends Frecuencia {

    public FrecuenciaRespiratoria(Double v) {
        super(validar(v));
    }

    private static Double validar(Double v) {
        if (v == null || v.isNaN() || v.isInfinite()||v < 0)
            throw new CampoInvalidoException("frecuenciaRespiratoria",
                    "debe tener valores positivos válidos (respiraciones por minuto)");
        return v;
    }
}
