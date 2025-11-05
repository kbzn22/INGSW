package com.grupo1.ingsw_app.domain.valueobjects;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

public class FrecuenciaCardiaca extends Frecuencia {

    public FrecuenciaCardiaca(Double v) {
        super(validar(v));
    }

    private static Double validar(Double v) {
        if (v == null || v.isNaN() || v.isInfinite()||v < 0)
            throw new CampoInvalidoException("frecuenciaCardiaca",
                    "debe tener valores positivos válidos (latidos por minuto)");
        return v;
    }
}
