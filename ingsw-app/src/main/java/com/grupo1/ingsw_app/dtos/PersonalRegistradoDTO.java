// src/main/java/com/grupo1/ingsw_app/dtos/PersonalRegistradoDTO.java
package com.grupo1.ingsw_app.dtos;

public class PersonalRegistradoDTO {

    private String cuil;
    private String nombre;
    private String apellido;
    private String rol;
    private String username;

    public PersonalRegistradoDTO(String cuil,
                                 String nombre,
                                 String apellido,
                                 String rol,
                                 String username) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.username = username;
    }

    public String getCuil()     { return cuil; }
    public String getNombre()   { return nombre; }
    public String getApellido() { return apellido; }
    public String getRol()      { return rol; }
    public String getUsername() { return username; }
}
