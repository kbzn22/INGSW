package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.Paciente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Servicio de dominio para la búsqueda de pacientes por DNI.
 * (Basado en principios de BDD in Action: lógica de negocio pura y testeable.)
 */
public class BuscadorPacientes {

    private final Map<String, Paciente> pacientes = new HashMap<>();

    /**
     * Registra pacientes en memoria (simula una base de datos).
     */
    public void registrarPacientes(List<Paciente> lista) {
        pacientes.clear();
        for (Paciente p : lista) {
            pacientes.put(p.getDni(), p);
        }
    }

    /**
     * Busca un paciente por su DNI.
     * @param dni DNI del paciente
     * @return el paciente encontrado
     * @throws IllegalArgumentException si el DNI tiene formato inválido
     * @throws NoSuchElementException si no se encuentra el paciente
     */
    public Paciente buscarPorDni(String dni) {
        if (dni == null || !dni.matches("\\d+")) {
            throw new IllegalArgumentException("DNI inválido");
        }

        Paciente p = pacientes.get(dni);
        if (p == null) {
            throw new NoSuchElementException("Paciente no encontrado");
        }
        return p;
    }
}
