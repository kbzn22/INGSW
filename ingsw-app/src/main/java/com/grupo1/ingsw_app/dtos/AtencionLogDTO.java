package com.grupo1.ingsw_app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class AtencionLogDTO {


    private UUID id;
    private UUID ingresoId;
    private String cuilDoctor;
    private String informe;
    private LocalDateTime fechaAtencion;


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

    public String getInforme() {
        return informe;
    }

    public String getCuilPaciente() {
        return cuilPaciente;
    }

    public Integer getNivel() {
        return nivel;
    }

}
