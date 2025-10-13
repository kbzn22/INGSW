package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IngresoService {

    private final IIngresoRepository repoIngreso;
    private final IPacienteRepository repoPaciente;

    public IngresoService(IIngresoRepository repoIngreso, IPacienteRepository repoPaciente) {
        this.repoIngreso = repoIngreso;
        this.repoPaciente = repoPaciente;
    }

    public void limpiarIngresos() {
        repoIngreso.clear();
    }

    public int posicionEnLaCola(String cuilPaciente) {
        /*List<Ingreso> ingresosPendientes = repo.findByEstado("PENDIENTE");
        ColaAtencion colaAtencion = new ColaAtencion();

        for(Ingreso ingreso : ingresosPendientes) {
            colaAtencion.agregar(ingreso);
        }

        return colaAtencion.posicionDe(cuilPaciente);*/
        return 0;

    }

    public Ingreso registrarIngreso(IngresoRequest req) {
        Paciente paciente = repoPaciente.findByCuil(req.getCuilPaciente())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        Enfermera enfermera = repoEnfermera.findByCuil(req.getCuilEnfermera())
                .orElseThrow(() -> new IllegalArgumentException("Enfermera no encontrada"));

        NivelEmergencia nivel = NivelEmergencia.fromNumero(req.getNivel());

        Ingreso ingreso = new Ingreso(paciente, enfermera, nivel);
        ingreso.setDescripcion(req.getInforme());
        ingreso.setTemperatura(new Temperatura(req.getTemperatura()));
        ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(req.getFrecuenciaCardiaca()));
        ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(req.getFrecuenciaRespiratoria()));
        ingreso.setTensionArterial(new TensionArterial(
                new Frecuencia(req.getFrecuenciaSistolica()),
                new Frecuencia(req.getFrecuenciaDiastolica())
        ));

        repoIngreso.save(ingreso);
        return ingreso;
    }

}
