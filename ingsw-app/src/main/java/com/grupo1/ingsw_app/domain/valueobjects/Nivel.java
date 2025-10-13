package com.grupo1.ingsw_app.domain.valueobjects;

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

    public int getNivel() {
        return nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public Duration getDuracionMaxEspera() {
        return duracionMaxEspera;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nivel)) return false;
        Nivel n = (Nivel) o;
        return nivel == n.nivel &&
                Objects.equals(nombre, n.nombre) &&
                Objects.equals(duracionMaxEspera, n.duracionMaxEspera);
    }

    @Override public int hashCode() {
        return Objects.hash(nivel);
    }
}
