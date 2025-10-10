package com.grupo1.ingsw_app.domain;

import java.time.Duration;
import java.util.Objects;

public class Nivel {
    private final int nivel;                 // 1..5
    private final String nombre;             // Rojo, Naranja, etc.
    private final Duration duracionMaxEspera;

    public Nivel(int nivel, String nombre, Duration duracionMaxEspera) {
        this.nivel = nivel;
        this.nombre = nombre;
        this.duracionMaxEspera = duracionMaxEspera;
    }

    public int getNivel() { return nivel; }
    public String getNombre() { return nombre; }
    public Duration getDuracionMaxEspera() { return duracionMaxEspera; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nivel)) return false;
        Nivel that = (Nivel) o;
        return nivel == that.nivel;
    }
    @Override public int hashCode() { return Objects.hash(nivel); }
}
