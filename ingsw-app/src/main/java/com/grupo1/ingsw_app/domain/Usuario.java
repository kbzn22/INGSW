package com.grupo1.ingsw_app.domain;

public class Usuario {
    private final String usuario;
    private final String password;

    public Usuario(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;

    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

}
