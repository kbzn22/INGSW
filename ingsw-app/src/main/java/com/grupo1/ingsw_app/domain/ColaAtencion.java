package com.grupo1.ingsw_app.domain;

import java.util.*;
import java.util.stream.IntStream;

public class ColaAtencion {

    private final List<ColaItem> ingresosEnCola = new ArrayList<>();

    public void limpiar() {
        ingresosEnCola.clear();
    }

    public void agregar(ColaItem colaItem) {
        ingresosEnCola.add(colaItem);
        ordenar();
    }

    public boolean estaElPaciente(String cuil) {
        return ingresosEnCola.stream()
                .anyMatch(item -> item.getCuil().equals(cuil));
    }

    /** Retorna posición 1-based; -1 si no está. */
    public int posicionDe(String cuilPaciente) {
        int idx = IntStream.range(0, ingresosEnCola.size())
                .filter(i -> ingresosEnCola.get(i).getCuil().equals(cuilPaciente))
                .findFirst().orElse(-1);
        return (idx == -1) ? -1 : idx + 1;
    }

    public List<ColaItem> verCola() {

        return Collections.unmodifiableList(ingresosEnCola);
    }

    private void ordenar() {
        ingresosEnCola.sort(
                Comparator.comparingInt((ColaItem i) -> i.getNivel())
                        .thenComparing(ColaItem::getFechaIngreso)
        );
    }
}
