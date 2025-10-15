package com.grupo1.ingsw_app.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;

public class IngresoRequest {

    private String cuilPaciente;
    private String cuilEnfermera;
    private String informe;

    // TIPOS ORIGINALES (WRAPPERS, no primitivos)
    private Float  temperatura;
    private Double frecuenciaCardiaca;
    private Double frecuenciaRespiratoria;
    private Double frecuenciaSistolica;
    private Double frecuenciaDiastolica;
    private Integer nivel;

    // -------- getters (no cambia nada) --------
    public String getCuilPaciente() { return cuilPaciente; }
    public String getCuilEnfermera() { return cuilEnfermera; }
    public String getInforme() { return informe; }
    public Float getTemperatura() { return temperatura; }
    public Double getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public Double getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public Double getFrecuenciaSistolica() { return frecuenciaSistolica; }
    public Double getFrecuenciaDiastolica() { return frecuenciaDiastolica; }
    public Integer getNivel() { return nivel; }

    // -------- setters “normales” para strings que no requieren coerción --------
    public void setCuilPaciente(String cuilPaciente) { this.cuilPaciente = cuilPaciente; }
    public void setCuilEnfermera(String cuilEnfermera) { this.cuilEnfermera = cuilEnfermera; }

    public void setInforme(String informe) {
        if (informe == null || informe.trim().isEmpty()) {
            throw new IllegalArgumentException("El informe es obligatorio y no puede estar vacío ni contener solo espacios");
        }
        this.informe = informe.trim();
    }

    // -------- setters tolerantes para numéricos (reciben Object) --------
    @JsonSetter("temperatura")
    public void setTemperatura(Object raw) {
        this.temperatura = parseFloat(raw, "La temperatura debe ser un número válido en grados Celsius");
        // Si querés además forzar no-negativo acá (además del VO), podés:
        if (this.temperatura < 0) {
            throw new IllegalArgumentException("La temperatura debe ser un número válido en grados Celsius");
        }
    }

    @JsonSetter("frecuenciaCardiaca")
    public void setFrecuenciaCardiaca(Object raw) {
        this.frecuenciaCardiaca = parseDouble(raw, "La frecuencia cardíaca debe ser un número válido (latidos por minuto)");
        if (this.frecuenciaCardiaca < 0) {
            throw new IllegalArgumentException("La frecuencia cardíaca no puede ser negativa");
        }
    }

    @JsonSetter("frecuenciaRespiratoria")
    public void setFrecuenciaRespiratoria(Object raw) {
        this.frecuenciaRespiratoria = parseDouble(raw, "La frecuencia respiratoria debe ser un número válido (respiraciones por minuto)");
        if (this.frecuenciaRespiratoria < 0) {
            throw new IllegalArgumentException("La frecuencia respiratoria no puede ser negativa");
        }
    }

    @JsonSetter("frecuenciaSistolica")
    public void setFrecuenciaSistolica(Object raw) {
        this.frecuenciaSistolica = parseDouble(raw, "La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
        if (this.frecuenciaSistolica < 0) {
            throw new IllegalArgumentException("La presión arterial no puede ser negativa");
        }
    }

    @JsonSetter("frecuenciaDiastolica")
    public void setFrecuenciaDiastolica(Object raw) {
        this.frecuenciaDiastolica = parseDouble(raw, "La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
        if (this.frecuenciaDiastolica < 0) {
            throw new IllegalArgumentException("La presión arterial no puede ser negativa");
        }
    }

    @JsonSetter("nivel")
    public void setNivel(Object raw) {
        this.nivel = parseInt(raw, "La prioridad ingresada no existe o es nula");
        if (this.nivel < 1 || this.nivel > 5) {
            throw new IllegalArgumentException("La prioridad ingresada no existe o es nula");
        }
    }

    // ----------------- helpers -----------------
    private static Float parseFloat(Object raw, String msg) {
        if (raw == null) throw new IllegalArgumentException(msg);
        if (raw instanceof Number n) return n.floatValue();
        if (raw instanceof String s) {
            try { return Float.parseFloat(s.trim()); }
            catch (Exception e) { throw new IllegalArgumentException(msg); }
        }
        throw new IllegalArgumentException(msg);
    }

    private static Double parseDouble(Object raw, String msg) {
        if (raw == null) throw new IllegalArgumentException(msg);
        if (raw instanceof Number n) return n.doubleValue();
        if (raw instanceof String s) {
            try { return Double.parseDouble(s.trim()); }
            catch (Exception e) { throw new IllegalArgumentException(msg); }
        }
        throw new IllegalArgumentException(msg);
    }

    private static Integer parseInt(Object raw, String msg) {
        if (raw == null) throw new IllegalArgumentException(msg);
        if (raw instanceof Number n) return n.intValue();
        if (raw instanceof String s) {
            try { return Integer.parseInt(s.trim()); }
            catch (Exception e) { throw new IllegalArgumentException(msg); }
        }
        throw new IllegalArgumentException(msg);
    }
}
