// src/main/java/com/grupo1/ingsw_app/dtos/AtencionLogDTO.java
package com.grupo1.ingsw_app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class AtencionLogDTO {


    private UUID id;
    private UUID ingresoId;
    private String cuilDoctor;
    private String informe;
    private LocalDateTime fechaAtencion;

    // info del ingreso asociada
    private String cuilPaciente;
    private String cuilEnfermera;
    private Integer nivel;
    private String estadoIngreso;
    private LocalDateTime fechaIngreso;

    public AtencionLogDTO(UUID id,
                          UUID ingresoId,
                          String cuilDoctor,
                          String informe,
                          LocalDateTime fechaAtencion,
                          String cuilPaciente,
                          String cuilEnfermera,
                          Integer nivel,
                          String estadoIngreso,
                          LocalDateTime fechaIngreso) {
        this.id = id;
        this.ingresoId = ingresoId;
        this.cuilDoctor = cuilDoctor;
        this.informe = informe;
        this.fechaAtencion = fechaAtencion;
        this.cuilPaciente = cuilPaciente;
        this.cuilEnfermera = cuilEnfermera;
        this.nivel = nivel;
        this.estadoIngreso = estadoIngreso;
        this.fechaIngreso = fechaIngreso;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIngresoId() {
        return ingresoId;
    }

    public String getCuilDoctor() {
        return cuilDoctor;
    }

    public String getInforme() {
        return informe;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public String getCuilPaciente() {
        return cuilPaciente;
    }

    public String getCuilEnfermera() {
        return cuilEnfermera;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getEstadoIngreso() {
        return estadoIngreso;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }
}
