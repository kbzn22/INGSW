// src/main/java/com/grupo1/ingsw_app/service/AtencionService.java
package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.dtos.PacienteEnAtencionDTO;
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

    public AtencionService(IIngresoRepository ingresoRepo,
                           IAtencionRepository atencionRepo,
                           Sesion sesionActual) {
        this.ingresoRepo = ingresoRepo;
        this.atencionRepo = atencionRepo;
        this.sesionActual = sesionActual;
    }
    private Doctor obtenerDoctorDeSesion() {
        Persona persona = sesionActual.getPersona(); // <- este método lo tenés seguro porque SesionRepository lo usa

        if (persona == null) {
            throw new IllegalStateException("No hay usuario autenticado en la sesión actual");
        }

        if (!(persona instanceof Doctor doctor)) {
            // si es Enfermera o cualquier otra cosa
            throw new IllegalStateException("El usuario actual no es un médico");
        }

        return doctor;
    }
    public PacienteEnAtencionDTO iniciarAtencion(UUID ingresoId) {

        Ingreso ingreso = ingresoRepo.findById(ingresoId)
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("ingreso", ingresoId.toString()));

        Doctor doctor = obtenerDoctorDeSesion();

        ingreso.setEstadoIngreso(EstadoIngreso.EN_PROCESO);
        ingresoRepo.save(ingreso);

        Atencion atencion = new Atencion(doctor, ingreso);
        atencion.setFechaAtencion(LocalDateTime.now());
        atencionRepo.save(atencion);

        return PacienteEnAtencionDTO.from(ingreso);
    }

    public PacienteEnAtencionDTO finalizarAtencion(UUID ingresoId, String informe) {

        Ingreso ingreso = ingresoRepo.findById(ingresoId)
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("ingreso", ingresoId.toString()));

        ingreso.setEstadoIngreso(EstadoIngreso.FINALIZADO);
        ingresoRepo.save(ingreso);

        Atencion atencion = atencionRepo.findByIngresoId(ingresoId)
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("atencion", "ingresoId=" + ingresoId));

        atencion.setInforme(informe);
        atencionRepo.save(atencion);

        return PacienteEnAtencionDTO.from(ingreso);
    }

    public Optional<PacienteEnAtencionDTO> obtenerPacienteEnAtencion() {
        return ingresoRepo.findEnAtencionActual()
                .map(PacienteEnAtencionDTO::from);
    }
}
