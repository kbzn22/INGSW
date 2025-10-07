package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Paciente;
import org.springframework.stereotype.Repository;

import java.util.*;
/**public PacienteService(IPacienteRepository repo) {
        this.repo = repo;
    }
 * Repositorio en memoria para gestionar pacientes.
 * Cumple la interfaz de persistencia mínima para los tests de BDD.
 */
@Repository
public class PacienteRepositoryInMemory implements IPacienteRepository {

    private final Map<String, Paciente> data = new HashMap<>();

    @Override
    public Optional<Paciente> findByDni(String dni) {
        return Optional.ofNullable(data.get(dni));
    }

    @Override
    public void save(Paciente paciente) {
        data.put(paciente.getDni(), paciente);
    }

   /* @Override
    public List<Paciente> findAll() {
        return new ArrayList<>(data.values());
    }*/

    public void clear() {
        data.clear();
    }
}
