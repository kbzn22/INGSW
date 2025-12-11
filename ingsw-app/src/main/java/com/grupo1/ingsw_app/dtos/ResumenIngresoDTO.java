package com.grupo1.ingsw_app.dtos;

public class ResumenIngresoDTO {

    private long SinUrgencia;     // pacientes en cola (PENDIENTE)
    private long UrgenciaMenor;   // pacientes EN_PROCESO
    private long Urgencia;     // nivel CRITICA
    private long Emergencia;
    private long Critica;

    public ResumenIngresoDTO(long sinUrgencia, long urgenciaMenor, long urgencia, long emergencia, long critica) {
        SinUrgencia = sinUrgencia;
        UrgenciaMenor = urgenciaMenor;
        Urgencia = urgencia;
        Emergencia = emergencia;
        Critica = critica;
    }
}
