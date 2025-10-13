package com.grupo1.ingsw_app.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public final class Cuil {

    private final String valor;

    @JsonCreator
    public Cuil(String valor) {
        if (valor == null || valor.isBlank() || !valor.trim().matches("\\d{2}-\\d{8}-\\d{1}")) {
            throw new IllegalArgumentException("CUIL inválido");
        }

        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuil)) return false;
        Cuil cuil = (Cuil) o;
        return valor.equals(cuil.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
