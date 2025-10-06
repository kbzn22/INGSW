package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Paciente;   // 👈 necesario para reconocer la clase
import java.util.*;

/**
 * Repositorio en memoria para gestionar pacientes.
 * Cumple la interfaz de persistencia mínima para los tests de BDD.
 */
public class PacienteRepository {

    private final Map<String, Paciente> porDni = new HashMap<>();

    /** Guarda un paciente individual. */
    public void save(Paciente p) {
        porDni.put(p.getDni(), p);
    }

    /** Guarda múltiples pacientes. */
    public void saveAll(List<Paciente> pacientes) {
        pacientes.forEach(this::save);
    }

    /** Busca un paciente por su DNI. */
    public Optional<Paciente> findByDni(String dni) {
        return Optional.ofNullable(porDni.get(dni));
    }

    /** Limpia el repositorio (por ejemplo, entre escenarios de Cucumber). */
    public void clear() {
        porDni.clear();
    }
}
