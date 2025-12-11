// src/main/java/com/grupo1/ingsw_app/service/AtencionService.java
package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.dtos.AtencionLogDTO;
import com.grupo1.ingsw_app.dtos.PacienteEnAtencionDTO;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.exception.DoctorYaTienePacienteEnAtencionException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.persistence.IAtencionRepository;
import com.grupo1.ingsw_app.persistence.IIngresoRepository;
import com.grupo1.ingsw_app.security.Sesion;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtencionService {

    private final IIngresoRepository ingresoRepo;
    private final IAtencionRepository atencionRepo;
    private final Sesion sesionActual;

    public AtencionService(IIngresoRepository ingresoRepo, IAtencionRepository atencionRepo, Sesion sesionActual) {
        this.ingresoRepo = ingresoRepo;
        this.atencionRepo = atencionRepo;
        this.sesionActual = sesionActual;
    }

    private Doctor obtenerDoctorDeSesion() {
        // misma idea que sesionActual.getEnfermera() en IngresoService
        return sesionActual.getDoctor();
    }

    public PacienteEnAtencionDTO iniciarAtencion(UUID ingresoId) {

        Doctor doctor = obtenerDoctorDeSesion();

        if(!ingresoRepo.findEnAtencionActual(doctor.getCuil().getValor()).isEmpty()){
            throw new DoctorYaTienePacienteEnAtencionException();
        }

        Ingreso ingreso = ingresoRepo.findById(ingresoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("ingreso", ingresoId.toString()));

        ingreso.setEstadoIngreso(EstadoIngreso.EN_PROCESO);
        ingresoRepo.save(ingreso);

        Atencion atencion = new Atencion(doctor, ingreso);
        atencionRepo.save(atencion);

        return PacienteEnAtencionDTO.from(ingreso);
    }

    public PacienteEnAtencionDTO finalizarAtencion(UUID ingresoId, String informe) {

        if (informe == null || informe.trim().isEmpty()) {
            throw new CampoInvalidoException("informe", "no puede estar vacío ni contener solo espacios.");
        }

        Ingreso ingreso = ingresoRepo.findById(ingresoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("ingreso", ingresoId.toString()));

        ingreso.setEstadoIngreso(EstadoIngreso.FINALIZADO);
        ingresoRepo.save(ingreso);

        Atencion atencion = atencionRepo.findByIngresoId(ingresoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("atencion", "ingresoId=" + ingresoId));

        atencion.setDoctor(obtenerDoctorDeSesion());
        atencion.setInforme(informe);
        atencionRepo.save(atencion);

        return PacienteEnAtencionDTO.from(ingreso);
    }

    public Optional<PacienteEnAtencionDTO> obtenerPacienteEnAtencion() {

        Doctor doctor = sesionActual.getDoctor();
        String cuilDoctor = doctor.getCuil().getValor();

        return ingresoRepo.findEnAtencionActual(cuilDoctor)
                .map(PacienteEnAtencionDTO::from);
    }

    public AtencionLogDTO obtenerDetalleAtencion(UUID idAtencion) {

        Atencion atencion = atencionRepo.findById(idAtencion)
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("atencion", idAtencion.toString()));

        Ingreso ingreso = ingresoRepo.findById(atencion.getIngreso().getId())
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("ingreso", atencion.getIngreso().getId().toString()));

        String cuilDoctor = atencion.getDoctor() != null && atencion.getDoctor().getCuil() != null
                ? atencion.getDoctor().getCuil().getValor()
                : null;

        String cuilPaciente = ingreso.getPaciente() != null && ingreso.getPaciente().getCuil() != null
                ? ingreso.getPaciente().getCuil().getValor()
                : null;

        String cuilEnfermera = ingreso.getEnfermera() != null && ingreso.getEnfermera().getCuil() != null
                ? ingreso.getEnfermera().getCuil().getValor()
                : null;

        return new AtencionLogDTO(
                atencion.getId(),
                ingreso.getId(),
                cuilDoctor,
                atencion.getInforme(),
                atencion.getFechaAtencion(),
                cuilPaciente,
                cuilEnfermera,
                ingreso.getNivelEmergencia() != null ? ingreso.getNivelEmergencia().getNumero() : null,
                ingreso.getEstadoIngreso() != null ? ingreso.getEstadoIngreso().name() : null,
                ingreso.getFechaIngreso()
        );
    }

}
