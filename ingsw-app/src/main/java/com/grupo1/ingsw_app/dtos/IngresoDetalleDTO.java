
package com.grupo1.ingsw_app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class IngresoDetalleDTO {

    private UUID idIngreso;

    // Paciente
    private String cuilPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String obraSocial;
    private String numeroAfiliado;

    // Enfermera que lo cargó
    private String cuilEnfermera;
    private String nombreEnfermera;
    private String apellidoEnfermera;

    // Ingreso
    private Integer nivel;
    private String nombreNivel;
    private String estado;
    private LocalDateTime fechaIngreso;
    private String informe;

    // Signos vitales
    private Float temperatura;
    private Double frecuenciaCardiaca;
    private Double frecuenciaRespiratoria;
    private Double sistolica;
    private Double diastolica;

    public IngresoDetalleDTO(
            UUID idIngreso,
            String cuilPaciente,
            String nombrePaciente,
            String apellidoPaciente,
            String obraSocial,
            String numeroAfiliado,
            String cuilEnfermera,
            String nombreEnfermera,
            String apellidoEnfermera,
            Integer nivel,
            String nombreNivel,
            String estado,
            LocalDateTime fechaIngreso,
            String informe,
            Float temperatura,
            Double frecuenciaCardiaca,
            Double frecuenciaRespiratoria,
            Double sistolica,
            Double diastolica
    ) {
        this.idIngreso = idIngreso;
        this.cuilPaciente = cuilPaciente;
        this.nombrePaciente = nombrePaciente;
        this.apellidoPaciente = apellidoPaciente;
        this.obraSocial = obraSocial;
        this.numeroAfiliado = numeroAfiliado;
        this.cuilEnfermera = cuilEnfermera;
        this.nombreEnfermera = nombreEnfermera;
        this.apellidoEnfermera = apellidoEnfermera;
        this.nivel = nivel;
        this.nombreNivel = nombreNivel;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
        this.informe = informe;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.sistolica = sistolica;
        this.diastolica = diastolica;
    }

    public UUID getIdIngreso() { return idIngreso; }
    public String getCuilPaciente() { return cuilPaciente; }
    public String getNombrePaciente() { return nombrePaciente; }
    public String getApellidoPaciente() { return apellidoPaciente; }
    public String getObraSocial() { return obraSocial; }
    public String getNumeroAfiliado() { return numeroAfiliado; }
    public String getCuilEnfermera() { return cuilEnfermera; }
    public String getNombreEnfermera() { return nombreEnfermera; }
    public String getApellidoEnfermera() { return apellidoEnfermera; }
    public Integer getNivel() { return nivel; }
    public String getNombreNivel() { return nombreNivel; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public String getInforme() { return informe; }
    public Float getTemperatura() { return temperatura; }
    public Double getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public Double getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public Double getSistolica() { return sistolica; }
    public Double getDiastolica() { return diastolica; }
}
