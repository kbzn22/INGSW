package com.grupo1.ingsw_app.domain;

import java.util.*;
import java.util.stream.IntStream;

public class ColaAtencion {

    private final List<Ingreso> ingresosEnCola = new ArrayList<>();

    public void limpiar() {
        ingresosEnCola.clear();
    }

    public void agregar(Ingreso ingreso) {
        ingresosEnCola.add(ingreso);
        ordenar();
    }

    public boolean estaElPaciente(String cuil) {
        return ingresosEnCola.stream()
                .anyMatch(i -> i.getPaciente().getCuil().getValor().equals(cuil));
    }

    /** Retorna posición 1-based; -1 si no está. */
    public int posicionDe(String cuilPaciente) {
        int idx = IntStream.range(0, ingresosEnCola.size())
                .filter(i -> ingresosEnCola.get(i).getPaciente().getCuil().getValor().equals(cuilPaciente))
                .findFirst().orElse(-1);
        return (idx == -1) ? -1 : idx + 1;
    }

    public List<Ingreso> verCola() {
        return Collections.unmodifiableList(ingresosEnCola);
    }

    private void ordenar() {
        ingresosEnCola.sort(
                Comparator.comparingInt((Ingreso i) -> i.getNivelEmergencia().getNivel().getNivel())
                        .thenComparing(Ingreso::getFechaIngreso)
        );
    }
}
