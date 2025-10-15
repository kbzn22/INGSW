package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.domain.Enfermera;
import org.springframework.stereotype.Component;

@Component
public class SesionActual {

    private Enfermera enfermeraActual;

    public Enfermera getEnfermeraActual() {
        return enfermeraActual;
    }

    public void setEnfermeraActual(Enfermera enfermeraActual) {
        if (enfermeraActual == null) {
            throw new IllegalStateException("No hay enfermera autenticada");
        }
        this.enfermeraActual = enfermeraActual;
    }

    public void limpiar() {
        this.enfermeraActual = null;
    }
}
