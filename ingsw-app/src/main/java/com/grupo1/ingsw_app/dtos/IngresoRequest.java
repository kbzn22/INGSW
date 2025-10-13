package com.grupo1.ingsw_app.dtos;

public class IngresoRequest {

    private String cuilPaciente;
    private String cuilEnfermera;
    private String informe;
    private float temperatura;
    private double frecuenciaCardiaca;
    private double frecuenciaRespiratoria;
    private double frecuenciaSistolica;
    private double frecuenciaDiastolica;
    private int nivel;

    // 🔹 Getters y setters obligatorios para que Jackson pueda deserializar el JSON
    public String getCuilPaciente() { return cuilPaciente; }
    public void setCuilPaciente(String cuilPaciente) { this.cuilPaciente = cuilPaciente; }

    public String getCuilEnfermera() { return cuilEnfermera; }
    public void setCuilEnfermera(String cuilEnfermera) { this.cuilEnfermera = cuilEnfermera; }

    public String getInforme() { return informe; }
    public void setInforme(String informe) { this.informe = informe; }

    public float getTemperatura() { return temperatura; }
    public void setTemperatura(float temperatura) { this.temperatura = temperatura; }

    public double getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(double frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public double getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public void setFrecuenciaRespiratoria(double frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }

    public double getFrecuenciaSistolica() { return frecuenciaSistolica; }
    public void setFrecuenciaSistolica(double frecuenciaSistolica) { this.frecuenciaSistolica = frecuenciaSistolica; }

    public double getFrecuenciaDiastolica() { return frecuenciaDiastolica; }
    public void setFrecuenciaDiastolica(double frecuenciaDiastolica) { this.frecuenciaDiastolica = frecuenciaDiastolica; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
}
