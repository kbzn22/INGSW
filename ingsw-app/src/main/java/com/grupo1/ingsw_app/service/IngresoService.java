package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ColaAtencion;
import com.grupo1.ingsw_app.domain.EstadoIngreso;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.NivelEmergencia;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.ColaItemDTO;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.dtos.ResumenColaDTO;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.persistence.IIngresoRepository;
import com.grupo1.ingsw_app.persistence.IPacienteRepository;
import com.grupo1.ingsw_app.security.Sesion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngresoService {

    private final IIngresoRepository repoIngreso;
    private final IPacienteRepository repoPaciente;
    private final Sesion sesionActual;
    private final ColaAtencion cola;


    public IngresoService(IIngresoRepository repoIngreso, IPacienteRepository repoPaciente, Sesion sesionActual) {
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
                .orElseThrow(() -> new EntidadNoEncontradaException("paciente", "CUIL: " + req.getCuilPaciente()));

        var enfermera = sesionActual.getEnfermera();

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
    public ResumenColaDTO obtenerResumenCola() {
        int pendientes  = repoIngreso.countByEstado(EstadoIngreso.PENDIENTE);
        int enAtencion  = repoIngreso.countByEstado(EstadoIngreso.EN_PROCESO);
        int finalizados = repoIngreso.countByEstado(EstadoIngreso.FINALIZADO);

        return new ResumenColaDTO(pendientes, enAtencion, finalizados);
    }

    public List<ColaItemDTO> obtenerColaDTO() {
        var ingresos = repoIngreso.findByEstadoPendiente(); // ya lo tenés
        return ingresos.stream()
                .map(ing -> new ColaItemDTO(
                        ing.getId(),
                        ing.getPaciente().getNombre(),
                        ing.getPaciente().getApellido(),// o nombre + apellido
                        ing.getPaciente().getCuil().getValor(),
                        ing.getNivelEmergencia().getNumero(),
                        ing.getEstadoIngreso().name(),
                        ing.getFechaIngreso()
                ))
                .toList();
    }

}
