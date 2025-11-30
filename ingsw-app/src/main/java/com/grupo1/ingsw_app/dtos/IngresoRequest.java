package com.grupo1.ingsw_app.dtos;

import java.util.UUID;

public class IngresoRequest {

    private String cuilPaciente;
    private String cuilEnfermera;
    private String informe;

    private Float  temperatura;
    private Double frecuenciaCardiaca;
    private Double frecuenciaRespiratoria;
    private Double frecuenciaSistolica;
    private Double frecuenciaDiastolica;
    private Integer nivel;



    public IngresoRequest(
            String cuilPaciente,
          //  String cuilEnfermera,
            String informe,
            Float temperatura,
            Double frecuenciaCardiaca,
            Double frecuenciaRespiratoria,
            Double frecuenciaSistolica,
            Double frecuenciaDiastolica,
            Integer nivel
    ) {
        this.cuilPaciente          = cuilPaciente;
        this.cuilEnfermera         = cuilEnfermera;
        this.informe               = informe;
        this.temperatura           = temperatura;
        this.frecuenciaCardiaca    = frecuenciaCardiaca;
        this.frecuenciaRespiratoria= frecuenciaRespiratoria;
        this.frecuenciaSistolica   = frecuenciaSistolica;
        this.frecuenciaDiastolica  = frecuenciaDiastolica;
        this.nivel                 = nivel;
    }



    public IngresoRequest(String cuilPaciente, String informe, Float temperatura, Double frecuenciaCardiaca, Double frecuenciaRespiratoria, Double frecuenciaSistolica, Double frecuenciaDiastolica, Integer nivel, UUID idObraSocial, String numeroAfiliado) {
    }

    public String getCuilPaciente() { return cuilPaciente; }
    public String getCuilEnfermera() { return cuilEnfermera; }
    public String getInforme() { return informe; }
    public Float getTemperatura() { return temperatura; }
    public Double getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public Double getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public Double getFrecuenciaSistolica() { return frecuenciaSistolica; }
    public Double getFrecuenciaDiastolica() { return frecuenciaDiastolica; }
    public Integer getNivel() { return nivel; }

    public void setCuilPaciente(String cuilPaciente) { this.cuilPaciente = cuilPaciente; }
    public void setCuilEnfermera(String cuilEnfermera) { this.cuilEnfermera = cuilEnfermera; }
    public void setInforme(String informe) { this.informe = informe; }
    public void setTemperatura(Float temperatura) { this.temperatura = temperatura; }
    public void setFrecuenciaCardiaca(Double frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }
    public void setFrecuenciaRespiratoria(Double frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }
    public void setFrecuenciaSistolica(Double frecuenciaSistolica) { this.frecuenciaSistolica = frecuenciaSistolica; }
    public void setFrecuenciaDiastolica(Double frecuenciaDiastolica) { this.frecuenciaDiastolica = frecuenciaDiastolica; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
}
