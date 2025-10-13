package com.grupo1.ingsw_app.domain;

import com.grupo1.ingsw_app.domain.valueobjects.FrecuenciaCardiaca;
import com.grupo1.ingsw_app.domain.valueobjects.FrecuenciaRespiratoria;
import com.grupo1.ingsw_app.domain.valueobjects.Temperatura;
import com.grupo1.ingsw_app.domain.valueobjects.TensionArterial;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ingreso {
    Atencion atencion;
    Paciente paciente;
    UUID id;
    Enfermera enfermera;
    NivelEmergencia nivelEmergencia;
    EstadoIngreso estadoIngreso;
    String descripcion;
    LocalDateTime fechaIngreso = LocalDateTime.now();
    Temperatura temperatura;
    TensionArterial tensionArterial;
    FrecuenciaCardiaca frecuenciaCardiaca;
    FrecuenciaRespiratoria frecuenciaRespiratoria;

    public Ingreso(Paciente paciente, Enfermera enfermera, NivelEmergencia nivelEmergencia) {
        this.paciente = paciente;
        this.enfermera = enfermera;
        this.nivelEmergencia = nivelEmergencia;
        this.id = UUID.randomUUID();
    }

    public Ingreso(Paciente paciente, NivelEmergencia nivelEmergencia, LocalDateTime fechaIngreso) {
        this.paciente = paciente;
        this.nivelEmergencia = nivelEmergencia;
        this.fechaIngreso = fechaIngreso;
        this.id = UUID.randomUUID();
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Enfermera getEnfermera() {
        return enfermera;
    }

    public void setEnfermera(Enfermera enfermera) {
        this.enfermera = enfermera;
    }

    public NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }

    public EstadoIngreso getEstadoIngreso() {
        return estadoIngreso;
    }

    public void setEstadoIngreso(EstadoIngreso estadoIngreso) {
        this.estadoIngreso = estadoIngreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Temperatura getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Temperatura temperatura) {
        this.temperatura = temperatura;
    }

    public TensionArterial getTensionArterial() {
        return tensionArterial;
    }

    public void setTensionArterial(TensionArterial tensionArterial) {
        this.tensionArterial = tensionArterial;
    }

    public FrecuenciaCardiaca getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(FrecuenciaCardiaca frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public FrecuenciaRespiratoria getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(FrecuenciaRespiratoria frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }
}
