package com.grupo1.ingsw_app.dtos;

public class PacienteRequest {

    private String cuil;
    private String nombre;
    private String apellido;
    private String email;
    private String calle;
    private String numero;
    private String localidad;
    private Long idObraSocial;       // puede ser null
    private String numeroAfiliado;   // puede ser null

    public PacienteRequest(
            String cuil,
            String nombre,
            String apellido,
            String email,
            String calle,
            String numero,
            String localidad,
            Long idObraSocial,
            String numeroAfiliado
    ) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.idObraSocial = idObraSocial;
        this.numeroAfiliado = numeroAfiliado;
    }

    public String getCuil() { return cuil; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getCalle() { return calle; }
    public String getNumero() { return numero; }
    public String getLocalidad() { return localidad; }
    public Long getIdObraSocial() { return idObraSocial; }
    public String getNumeroAfiliado() { return numeroAfiliado; }
}
