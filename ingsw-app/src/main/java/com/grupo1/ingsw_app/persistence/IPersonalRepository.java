package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Persona;

import java.util.List;
import java.util.Optional;

public interface IPersonalRepository {
    public void save(Persona persona);

    public void clear();

    public Optional<Persona> findByUsername(String username);

    public Optional<Persona> findByMatricula(String matricula);

    public Optional<Persona> findByCuil(String cuil);

    public Optional<Doctor> findDoctorByUsername(String username);

    public Optional<Enfermera> findEnfermeraByUsername(String username);

    public List<Doctor> findAllDoctors();

    public List<Enfermera> findAllEnfermeras();
}
