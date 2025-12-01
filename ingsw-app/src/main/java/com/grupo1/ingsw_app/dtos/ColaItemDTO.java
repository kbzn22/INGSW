// src/main/java/com/grupo1/ingsw_app/dtos/ColaItemDTO.java
package com.grupo1.ingsw_app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ColaItemDTO {

    private UUID idIngreso;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String cuilPaciente;
    private Integer nivel;
    private String estado;
    private LocalDateTime fechaIngreso;

    public ColaItemDTO(UUID idIngreso,
                       String nombrePaciente,
                       String apellidoPaciente,
                       String cuilPaciente,
                       Integer nivel,
                       String estado,
                       LocalDateTime fechaIngreso) {
        this.idIngreso = idIngreso;
        this.nombrePaciente = nombrePaciente;
        this.apellidoPaciente=apellidoPaciente;
        this.cuilPaciente = cuilPaciente;
        this.nivel = nivel;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
    }

    public UUID getIdIngreso() { return idIngreso; }
    public String getNombrePaciente() { return nombrePaciente; }
    public String getApellidoPaciente() { return apellidoPaciente; }
    public String getCuilPaciente() { return cuilPaciente; }
    public Integer getNivel() { return nivel; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
}
