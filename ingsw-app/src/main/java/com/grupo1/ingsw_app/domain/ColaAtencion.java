package com.grupo1.ingsw_app.domain;

import java.util.*;
import java.util.stream.IntStream;

public class ColaAtencion {

    private final List<Ingreso> ingresos = new ArrayList<>();

    public void agregar(Ingreso i) {
        ingresos.add(i);
        ordenar();
    }

    private void ordenar() {
        ingresos.sort(
                Comparator.comparingInt((Ingreso i) -> i.getNivelEmergencia().getNivel().getNivel())
                        .thenComparing(Ingreso::getFechaIngreso)
        );
    }
    public List<Ingreso> ver() {
        return Collections.unmodifiableList(ingresos);
    }

    public boolean contiene(String cuil) {
        return ingresos.stream()
                .anyMatch(i -> i.getPaciente().getCuil().getValor().equals(cuil));
    }
    public int posicionDe(String cuil) {
        for (int i = 0; i < ingresos.size(); i++) {
            if (ingresos.get(i).getPaciente().getCuil().getValor().equals(cuil)) {
                return i + 1;
            }
        }
        return 0; // No encontrado
    }
}
