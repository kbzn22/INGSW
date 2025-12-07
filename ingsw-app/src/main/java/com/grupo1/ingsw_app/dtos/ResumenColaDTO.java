package com.grupo1.ingsw_app.dtos;

public class ResumenColaDTO {

    private int pendientes;
    private int enAtencion;
    private int finalizados;

    public ResumenColaDTO(int pendientes, int enAtencion, int finalizados) {
        this.pendientes = pendientes;
        this.enAtencion = enAtencion;
        this.finalizados = finalizados;
    }

    public int getPendientes() {
        return pendientes;
    }

    public int getEnAtencion() {
        return enAtencion;
    }

    public int getFinalizados() {
        return finalizados;
    }
}
