package org.example.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Ingreso {
    Paciente paciente;
    Enfermera enfermera;
    LocalDateTime fechaIngreso;
    String informe;
    NivelEmergencia nivelEmergencia;
    EstadoIngreso estado;
    Float temperatura;
    Float frecuenciaCardiaca;
    Float frecuenciaRespiratoria;
    Float frecuenciaDiastolica;
    Float frecuenciaSistolica;

    public Ingreso(Paciente paciente,
                   Enfermera enfermera,
                   String informe,
                   NivelEmergencia nivelEmergencia,
                   Float temperatura,
                   Float frecuenciaCardiaca,
                   Float frecuenciaRespiratoria,
                   Float frecuenciaSistolica,
                   Float frecuenciaDiastolica) {
        this.paciente = paciente;
        this.enfermera = enfermera;
        this.fechaIngreso = LocalDateTime.now();
        this.informe = informe;
        this.nivelEmergencia = nivelEmergencia;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.frecuenciaSistolica = frecuenciaSistolica;
        this.frecuenciaDiastolica = frecuenciaDiastolica;
        this.estado = estado.PENDIENTE;
    }
    public String getCuilPaciente(){
        return this.paciente.getCuil();
    }
    public EstadoIngreso getEstado() { return this.estado; }
    public Enfermera getEnfermera() { return this.enfermera; }
    public NivelEmergencia getNivelEmergencia() { return nivelEmergencia; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
}
