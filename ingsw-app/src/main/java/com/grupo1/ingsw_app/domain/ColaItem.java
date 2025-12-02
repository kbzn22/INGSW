package com.grupo1.ingsw_app.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class ColaItem {

    private UUID idIngreso;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String cuilPaciente;
    private Integer nivel;
    private LocalDateTime fechaIngreso;

    public ColaItem(UUID idIngreso,
                    String nombrePaciente,
                    String apellidoPaciente,
                    String cuilPaciente,
                    Integer nivel,
                    LocalDateTime fechaIngreso) {
        this.idIngreso = idIngreso;
        this.nombrePaciente = nombrePaciente;
        this.apellidoPaciente=apellidoPaciente;
        this.cuilPaciente = cuilPaciente;
        this.nivel = nivel;
        this.fechaIngreso = fechaIngreso;
    }

    public ColaItem(String nombrePaciente,
                    String cuilPaciente,
                    Integer nivel,
                    LocalDateTime fechaIngreso) {
        this.nombrePaciente = nombrePaciente;
        this.cuilPaciente = cuilPaciente;
        this.nivel = nivel;
        this.fechaIngreso = fechaIngreso;
    }

    public UUID getIdIngreso() { return idIngreso; }
    public String getNombrePaciente() { return nombrePaciente; }
    public String getApellidoPaciente() { return apellidoPaciente; }
    public String getCuilPaciente() { return cuilPaciente; }
    public Integer getNivel() { return nivel; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
}
