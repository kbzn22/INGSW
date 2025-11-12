package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Paciente;
import org.springframework.stereotype.Repository;

import java.util.*;

/* Repositorio en memoria para gestionar pacientes.
 * Cumple la interfaz de persistencia mínima para los tests de BDD.
 */
@Repository
public class PacienteRepositoryInMemory implements IPacienteRepository {

    private final Map<String, Paciente> data = new HashMap<>();

    @Override
    public Optional<Paciente> findByCuil(String cuil) {
        return Optional.ofNullable(data.get(cuil));
    }

    @Override
    public void save(Paciente paciente) {
        data.put(paciente.getCuil().getValor(), paciente);
    }



    public void clear() {
        data.clear();
    }
}
