// src/main/java/com/grupo1/ingsw_app/persistence/PersonalRepository.java
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio unificado para todo el personal de salud (Doctores, Enfermeras, etc.).
 * Permite buscar por username, matrícula o CUIL sin duplicar código.
 */
@Component
public class PersonalRepository {

    // índices en memoria
    private final Map<String, Persona> byUsername = new ConcurrentHashMap<>();
    private final Map<String, Persona> byMatricula = new ConcurrentHashMap<>();
    private final Map<String, Persona> byCuil = new ConcurrentHashMap<>();

    /** Guarda o actualiza cualquier persona (Doctor o Enfermera). */
    public void save(Persona persona) {
        if (persona instanceof Enfermera enfermera && enfermera.getUsuario() != null) {
            byUsername.put(enfermera.getUsuario().getUsuario(), enfermera);
        } else if (persona instanceof Doctor doctor && doctor.getUsuario() != null) {
            byUsername.put(doctor.getUsuario().getUsuario(), doctor);
        }

        if (persona instanceof Enfermera e && e.getMatricula() != null) {
            byMatricula.put(e.getMatricula(), e);
        } else if (persona instanceof Doctor d && d.getMatricula() != null) {
            byMatricula.put(d.getMatricula(), d);
        }

        if (persona.getCuil() != null) {
            byCuil.put(persona.getCuil().getValor(), persona);
        }
    }

    public void clear() {
        byUsername.clear();
        byMatricula.clear();
        byCuil.clear();
    }


    public Optional<Persona> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }

    public Optional<Persona> findByMatricula(String matricula) {
        return Optional.ofNullable(byMatricula.get(matricula));
    }

    public Optional<Persona> findByCuil(String cuil) {
        return Optional.ofNullable(byCuil.get(cuil));
    }

    public Optional<Doctor> findDoctorByUsername(String username) {
        Persona p = byUsername.get(username);
        return (p instanceof Doctor d) ? Optional.of(d) : Optional.empty();
    }

    public Optional<Enfermera> findEnfermeraByUsername(String username) {
        Persona p = byUsername.get(username);
        return (p instanceof Enfermera e) ? Optional.of(e) : Optional.empty();
    }

    public List<Doctor> findAllDoctors() {
        return byUsername.values().stream()
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast)
                .toList();
    }

    public List<Enfermera> findAllEnfermeras() {
        return byUsername.values().stream()
                .filter(Enfermera.class::isInstance)
                .map(Enfermera.class::cast)
                .toList();
    }
}
