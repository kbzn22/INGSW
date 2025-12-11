package com.grupo1.ingsw_app.dtos;

public class IngresoRequest {

    private String cuilPaciente;
    private String informe;
    private Double  temperatura;
    private Double frecuenciaCardiaca;
    private Double frecuenciaRespiratoria;
    private Double frecuenciaSistolica;
    private Double frecuenciaDiastolica;
    private Integer nivel;



    public IngresoRequest(
            String cuilPaciente,
            String informe,
            Double temperatura,
            Double frecuenciaCardiaca,
            Double frecuenciaRespiratoria,
            Double frecuenciaSistolica,
            Double frecuenciaDiastolica,
            Integer nivel
    ) {
        this.cuilPaciente          = cuilPaciente;
        this.informe               = informe;
        this.temperatura           = temperatura;
        this.frecuenciaCardiaca    = frecuenciaCardiaca;
        this.frecuenciaRespiratoria= frecuenciaRespiratoria;
        this.frecuenciaSistolica   = frecuenciaSistolica;
        this.frecuenciaDiastolica  = frecuenciaDiastolica;
        this.nivel                 = nivel;
    }


    public String getCuilPaciente() { return cuilPaciente; }
    public String getInforme() { return informe; }
    public Double getTemperatura() { return temperatura; }
    public Double getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public Double getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public Double getFrecuenciaSistolica() { return frecuenciaSistolica; }
    public Double getFrecuenciaDiastolica() { return frecuenciaDiastolica; }
    public Integer getNivel() { return nivel; }

    public void setCuilPaciente(String cuilPaciente) { this.cuilPaciente = cuilPaciente; }
    public void setInforme(String informe) { this.informe = informe; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public void setNivel(Integer nivel) { this.nivel = nivel; }
}
