
package com.grupo1.ingsw_app.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Atencion {

    private UUID id;
    private Doctor doctor;
    private Ingreso ingreso;
    private String informe;
    private LocalDateTime fechaAtencion;

    public Atencion(Doctor doctor, Ingreso ingreso) {
        this.id = UUID.randomUUID();
        this.doctor = doctor;
        this.ingreso = ingreso;
        this.fechaAtencion = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Ingreso getIngreso() { return ingreso; }
    public void setIngreso(Ingreso ingreso) { this.ingreso = ingreso; }

    public String getInforme() { return informe; }
    public void setInforme(String informe) { this.informe = informe; }

    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public void setFechaAtencion(LocalDateTime fechaAtencion) { this.fechaAtencion = fechaAtencion; }
}
