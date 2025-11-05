package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.domain.Enfermera;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SesionActual {

    private final String id;
    private final String usuario;  // dueño
    private final Instant expiresAt;

    private Enfermera enfermeraActual;

    public Enfermera getEnfermeraActual() {
        return enfermeraActual;
    }

    public void setEnfermeraActual(Enfermera enfermeraActual) {
        if (enfermeraActual == null) {
            throw new IllegalStateException("No hay enfermera autenticada");
        }
        this.enfermeraActual = enfermeraActual;
    }

    public void limpiar() {
        this.enfermeraActual = null;
    }

    public SesionActual(String id, String username, Instant expiresAt) {
        this.id = id;
        this.usuario  = username;
        this.expiresAt = expiresAt;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}
