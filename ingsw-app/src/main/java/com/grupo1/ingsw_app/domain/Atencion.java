package com.grupo1.ingsw_app.domain;

public class Atencion {
    Doctor doctor;
    String informe;

    public Atencion(String informe, Doctor doctor) {
        this.informe = informe;
        this.doctor = doctor;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
