package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.domain.ColaItem;
import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import com.grupo1.ingsw_app.dtos.ResumenColaDTO;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.exception.PacienteRedundanteEnColaException;
import com.grupo1.ingsw_app.external.IObraSocialClient;
import com.grupo1.ingsw_app.external.ObraSocialClient;
import com.grupo1.ingsw_app.persistence.IIngresoRepository;
import com.grupo1.ingsw_app.persistence.IPacienteRepository;
import com.grupo1.ingsw_app.security.Sesion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IngresoService {

    private final IIngresoRepository repoIngreso;
    private final IPacienteRepository repoPaciente;
    private final Sesion sesionActual;
    private IObraSocialClient obraSocialClient;

    public IngresoService(IIngresoRepository repoIngreso, IPacienteRepository repoPaciente, Sesion sesionActual, ObraSocialClient obraSocialClient) {
        this.repoIngreso = repoIngreso;
        this.repoPaciente = repoPaciente;
        this.sesionActual = sesionActual;
        this.obraSocialClient = obraSocialClient;
    }

    public Ingreso obtenerIngreso(UUID ingresoId) { //revisado
        Ingreso ingreso = repoIngreso.findById(ingresoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("ingreso", ingresoId.toString()));
        return ingreso;
    }

    public Ingreso registrarIngreso(IngresoRequest request) { //revisado

        if (request.getInforme() == null || request.getInforme().trim().isEmpty()) {
            throw new CampoInvalidoException("informe", "no puede estar vacío ni contener solo espacios");
        }

        String cuil = request.getCuilPaciente();

        var paciente = repoPaciente.findByCuil(cuil)
                .orElseThrow(() -> new EntidadNoEncontradaException("paciente", "CUIL: " + cuil));

        var enfermera = sesionActual.getEnfermera();

        List<Ingreso> pendientes = repoIngreso.findByEstado(EstadoIngreso.PENDIENTE);

        boolean yaEnCola = pendientes.stream()
                .anyMatch(ing -> ing.getPaciente().getCuil().getValor().equals(cuil));

        if(yaEnCola){
            throw new PacienteRedundanteEnColaException(cuil);
        }

        Ingreso ingreso = new Ingreso(
                paciente,
                enfermera,
                request.getNivel(),
                request.getInforme(),
                request.getTemperatura(),
                request.getFrecuenciaSistolica(),
                request.getFrecuenciaDiastolica(),
                request.getFrecuenciaCardiaca(),
                request.getFrecuenciaRespiratoria()
        );

        repoIngreso.save(ingreso);

        return ingreso;
    }

    public ResumenColaDTO obtenerResumenCola() { //revisado
        int pendientes  = repoIngreso.countByEstado(EstadoIngreso.PENDIENTE);
        int enAtencion  = repoIngreso.countByEstado(EstadoIngreso.EN_PROCESO);
        int finalizados = repoIngreso.countByEstado(EstadoIngreso.FINALIZADO);

        return new ResumenColaDTO(pendientes, enAtencion, finalizados);
    }

    public List<ColaItem> obtenerColaPendiente() { //revisado
        List<Ingreso> pendientes = repoIngreso.findByEstado(EstadoIngreso.PENDIENTE);

        ColaAtencion cola = new ColaAtencion();

        for (Ingreso ingreso : pendientes) {

            ColaItem item = new ColaItem(
                    ingreso.getId(),
                    ingreso.getPaciente().getNombre(),
                    ingreso.getPaciente().getApellido(),
                    ingreso.getPaciente().getCuil().getValor(),
                    ingreso.getNivelEmergencia().getNumero(),
                    ingreso.getEstadoIngreso().name(),
                    ingreso.getNivelEmergencia().name(),
                    ingreso.getFechaIngreso()
            );

            cola.agregar(item);
        }

        return cola.verCola();
    }

    public List<ObraSocialDto> listarObrasSociales() { //revisado
        return obraSocialClient.listarObrasSociales();
    }
}
