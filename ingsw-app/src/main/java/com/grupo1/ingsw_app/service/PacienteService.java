package com.grupo1.ingsw_app.service;

import org.springframework.stereotype.Service;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import com.grupo1.ingsw_app.domain.Paciente;

import java.util.Optional;

@Service
public class PacienteService {

    private final IPacienteRepository repo;

    public PacienteService(IPacienteRepository repo) {
        this.repo = repo;
    }

    public Paciente buscarPorDni(String dni) {
        if (dni == null || !dni.matches("\\d+")) {
            throw new IllegalArgumentException("DNI inválido");
        }

        Optional<Paciente> paciente = repo.findByDni(dni);

        if (paciente.isEmpty()) {
            throw new RuntimeException("Paciente no encontrado");
        }

        return paciente.get();
    }
}
