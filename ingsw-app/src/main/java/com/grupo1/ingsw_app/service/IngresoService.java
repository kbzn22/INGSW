package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ColaAtencion;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.NivelEmergencia;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.exception.PacienteNoEncontradoException;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import com.grupo1.ingsw_app.security.SesionActual;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngresoService {

    private final IIngresoRepository repoIngreso;
    private final IPacienteRepository repoPaciente;
    private final SesionActual sesionActual;
    private final ColaAtencion cola;


    public IngresoService(IIngresoRepository repoIngreso, IPacienteRepository repoPaciente, SesionActual sesionActual) {
        this.repoIngreso = repoIngreso;
        this.repoPaciente = repoPaciente;
        this.sesionActual = sesionActual;
        this.cola = new ColaAtencion();
    }

    public Ingreso registrarIngreso(IngresoRequest req) {

        if (req.getInforme() == null || req.getInforme().trim().isEmpty()) {
            throw new CampoInvalidoException("informe", "no puede estar vacío ni contener solo espacios");
        }

        var paciente = repoPaciente.findByCuil(req.getCuilPaciente())
                .orElseThrow(() -> new PacienteNoEncontradoException(req.getCuilPaciente()));

        var enfermera = sesionActual.getEnfermeraActual();
        var nivel = NivelEmergencia.fromNumero(req.getNivel());
        String informe = req.getInforme();

        Ingreso ingreso = new Ingreso(paciente, enfermera, nivel);
        ingreso.setDescripcion(informe);
        ingreso.setTemperatura(new Temperatura(req.getTemperatura()));
        ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(req.getFrecuenciaCardiaca()));
        ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(req.getFrecuenciaRespiratoria()));
        ingreso.setTensionArterial(new TensionArterial(
                (req.getFrecuenciaSistolica()),
                (req.getFrecuenciaDiastolica())
        ));

        repoIngreso.save(ingreso);
        cola.agregar(ingreso);
        return ingreso;
    }

    public ColaAtencion obtenerCola () {
        cola.limpiar();
        List<Ingreso> ingresos = repoIngreso.findByEstadoPendiente();

        for(Ingreso ingreso: ingresos){
            cola.agregar(ingreso);
        }

        return cola;
    }

}
