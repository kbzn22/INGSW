package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.Nivel;

import java.time.Duration;

/** Enum asociado 1–1 a un Nivel estático (no necesita repositorio). */
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

    // Utilidad: buscar por número de nivel (1..5).
    public static NivelEmergencia fromNumero(int numero) {
        for (NivelEmergencia ne : values()) {
            if (ne.getNumero() == numero) return ne;
        }
        throw new IllegalArgumentException("Nivel de emergencia inválido: " + numero);
    }
}
