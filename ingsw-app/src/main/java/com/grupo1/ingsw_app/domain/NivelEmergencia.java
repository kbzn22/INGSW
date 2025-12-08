package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Nivel;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;

import java.time.Duration;


public enum NivelEmergencia {
    CRITICA     (new Nivel(1, "Rojo",       Duration.ZERO)),
    EMERGENCIA  (new Nivel(2, "Naranja",    Duration.ofMinutes(10))),
    URGENCIA    (new Nivel(3, "Amarillo",   Duration.ofMinutes(30))),
    URGENCIA_MENOR(new Nivel(4, "Verde",    Duration.ofHours(1))),
    SIN_URGENCIA(new Nivel(5, "Azul",       Duration.ofHours(2)));

    private final Nivel nivel;

    NivelEmergencia(Nivel nivel) {
        this.nivel = nivel;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public int getNumero() {
        return nivel.getNivel();
    }

    public String getNombreEnum() {
        return this.name();
    }

    public static NivelEmergencia fromNumero(Integer numero) {
        if (numero == null) {
            throw new CampoInvalidoException(
                    "nivel",
                    "la prioridad ingresada no existe o es nula"
            );
        }

        switch (numero) {
            case 1: return CRITICA;
            case 2: return EMERGENCIA;
            case 3: return URGENCIA;
            case 4: return URGENCIA_MENOR;
            case 5: return SIN_URGENCIA;
            default: throw new CampoInvalidoException("nivel",
                    "la prioridad ingresada no existe o es nula");
        }
    }
}

