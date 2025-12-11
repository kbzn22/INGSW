package com.grupo1.ingsw_app.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class ColaItem {

    private UUID   ingresoId;
    private String nombre;
    private String apellido;
    private String cuil;
    private int    nivel;
    private String estado;
    private String nombreNivel;
    private LocalDateTime fechaIngreso;

    public ColaItem(
            UUID ingresoId,
            String nombre,
            String apellido,
            String cuil,
            int nivel,
            String estado,
            String nombreNivel,
            LocalDateTime fechaIngreso
    ) {
        this.ingresoId = ingresoId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cuil = cuil;
        this.nivel = nivel;
        this.estado = estado;
        this.nombreNivel = nombreNivel;
        this.fechaIngreso = fechaIngreso;
    }

    public UUID getIngresoId(){
        return ingresoId;
    }

    public String getNombre(){
        return nombre;
    }

    public String getApellido(){
        return apellido;
    }

    public String getCuil(){
        return cuil;
    }

    public int getNivel(){
        return nivel;
    }


    public LocalDateTime getFechaIngreso(){
        return fechaIngreso;
    }
}
