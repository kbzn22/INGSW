package com.grupo1.ingsw_app.service;

import org.springframework.stereotype.Service;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

import java.util.Optional;

@Service
public class PacienteService {

    private final IPacienteRepository repo;

    public PacienteService(IPacienteRepository repo) {
        this.repo = repo;
    }

    public Paciente buscarPorCuil(String cuilString) {

        Cuil cuil = new Cuil(cuilString);

        Optional<Paciente> paciente = repo.findByCuil(cuil.getValor());

        if (paciente.isEmpty()) {
            throw new RuntimeException("Paciente no encontrado");
        }

        return paciente.get();
    }
}
