// src/main/java/com/grupo1/ingsw_app/dtos/PacienteEnAtencionDTO.java
package com.grupo1.ingsw_app.dtos;

import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.Paciente;

import java.time.LocalDateTime;
import java.util.UUID;

public class PacienteEnAtencionDTO {

    private UUID idIngreso;
    private String nombre;
    private String apellido;
    private String cuil;
    private Integer nivel;
    private String estado;
    private LocalDateTime fechaIngreso;
    private String informe;

    public PacienteEnAtencionDTO(UUID idIngreso,
                                 String nombre,
                                 String apellido,
                                 String cuil,
                                 Integer nivel,
                                 String estado,
                                 LocalDateTime fechaIngreso,
                                 String informe) {
        this.idIngreso = idIngreso;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cuil = cuil;
        this.nivel = nivel;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
        this.informe = informe;
    }

    // Getters (no hace falta setters para el JSON de salida)

    public UUID getIdIngreso() {
        return idIngreso;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCuil() {
        return cuil;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public String getInforme() {
        return informe;
    }

    // ====== Factory estático que usa Ingreso del dominio ======

    public static PacienteEnAtencionDTO from(Ingreso ingreso) {
        if (ingreso == null) return null;

        Paciente paciente = ingreso.getPaciente();

        String cuil = null;
        String nombre = null;
        String apellido = null;
        if (paciente != null) {
            nombre = paciente.getNombre();
            apellido = paciente.getApellido();
            if (paciente.getCuil() != null) {
                cuil = paciente.getCuil().getValor();
            }
        }

        Integer nivel = ingreso.getNivelEmergencia() != null
                ? ingreso.getNivelEmergencia().getNumero()
                : null;

        String estado = ingreso.getEstadoIngreso() != null
                ? ingreso.getEstadoIngreso().name()
                : null;

        LocalDateTime fecha = ingreso.getFechaIngreso();

        String informe = ingreso.getDescripcion(); // usamos la descripción como informe base

        return new PacienteEnAtencionDTO(
                ingreso.getId(),
                nombre,
                apellido,
                cuil,
                nivel,
                estado,
                fecha,
                informe
        );
    }
}
