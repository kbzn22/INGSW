package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Paciente;

import java.util.*;

public interface IPacienteRepository {

    Optional<Paciente> findByCuil(String cuil);
    void save(Paciente paciente);
    void clear();
    //List<Paciente> findAll();


}
