// src/main/java/com/grupo1/ingsw_app/security/Sesion.java
package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Persona;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Component
@Scope("sesion") // una instancia por sesión HTTP
public class Sesion {

    private String id;
    private String usuario;
    private Instant expiresAt;
    private Persona persona;

    // ---- API de gestión ----
    public void iniciar(String usuario, Persona persona, long horas) {
        this.id = nuevoId();
        this.usuario = usuario;
        this.persona = persona;
        this.expiresAt = Instant.now().plus(horas, ChronoUnit.HOURS);
    }

    public void limpiar() {
        this.id = null;
        this.usuario = null;
        this.persona = null;
        this.expiresAt = null;
    }

    public boolean isExpired() {
        return expiresAt == null || Instant.now().isAfter(expiresAt);
    }

    // ---- helpers ----
    private static String nuevoId() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ---- getters ----
    public String getId() { return id; }
    public String getUsuario() { return usuario; }
    public Instant getExpiresAt() { return expiresAt; }

    public Enfermera getEnfermera() {
        if (persona instanceof Enfermera e) return e;
        throw new SecurityException("Se requiere rol ENFERMERIA");
    }

    public void setUsuario(Persona persona) {
        if (persona == null)
            throw new IllegalArgumentException("No se puede asignar null a la sesión");

        this.persona = persona;

        // detecta el tipo concreto y saca el username del Usuario embebido
        if (persona instanceof Doctor d && d.getUsuario() != null) {
            this.usuario = d.getUsuario().getUsuario();
        } else if (persona instanceof Enfermera e && e.getUsuario() != null) {
            this.usuario = e.getUsuario().getUsuario();
        } else {
            throw new IllegalStateException("La persona no tiene Usuario asociado");
        }

        this.id = generarId();
        this.expiresAt = Instant.now().plus(2, ChronoUnit.HOURS);
    }

    public Persona getPersona() {
        if (persona == null)
            throw new IllegalStateException("No hay persona autenticada en la sesión");
        return persona;
    }
    private static String generarId() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
