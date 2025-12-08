package com.grupo1.ingsw_app.dtos;

public class AtencionResumenDTO {

    private long enEspera;     // pacientes en cola (PENDIENTE)
    private long enAtencion;   // pacientes EN_PROCESO
    private long criticos;     // nivel CRITICA
    private long emergencias;  // nivel EMERGENCIA

    public AtencionResumenDTO(long enEspera, long enAtencion, long criticos, long emergencias) {
        this.enEspera = enEspera;
        this.enAtencion = enAtencion;
        this.criticos = criticos;
        this.emergencias = emergencias;
    }

    public long getEnEspera()     { return enEspera; }
    public long getEnAtencion()   { return enAtencion; }
    public long getCriticos()     { return criticos; }
    public long getEmergencias()  { return emergencias; }
}
