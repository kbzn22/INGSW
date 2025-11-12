// src/main/java/com/grupo1/ingsw_app/service/AutenticacionService.java
package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.persistence.PersonalRepository;
import com.grupo1.ingsw_app.persistence.SesionRepository;
import com.grupo1.ingsw_app.security.Sesion;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;

public class AutenticacionService {

    private final PersonalRepository personalRepo;
    private final Sesion sesion;
    private final PasswordEncoder encoder;

    public AutenticacionService(PersonalRepository personalRepo, Sesion sesion, PasswordEncoder encoder) {
        this.personalRepo = personalRepo;
        this.sesion = sesion;
        this.encoder = encoder;
    }

    /** login: valida usuario/password, crea sesión y devuelve id */
    public String login(String username, String rawPassword) {
        Persona persona = personalRepo.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("El usuario no existe"));

        Usuario cuenta = (persona instanceof Doctor d) ? d.getUsuario()
                : (persona instanceof Enfermera e) ? e.getUsuario()
                : null;

        if (cuenta == null)
            throw new IllegalStateException("El personal no tiene una cuenta de usuario asociada");

        if (!encoder.matches(rawPassword, cuenta.getPassword())
                && !cuenta.getPassword().equals(rawPassword)) {
            throw new IllegalArgumentException("La contraseña es incorrecta");
        }


        sesion.iniciar(cuenta.getUsuario(), persona, 2L); // 2 horas

        return sesion.getId();
    }

    /** obtiene el Usuario vinculado a una sesión */
    public Usuario requireSession(String sessionId) {
        String currentId = sesion.getId();
        if (sessionId == null || sesion.getId() == null || !sessionId.equals(sesion.getId()) || sesion.isExpired()) {
            throw new IllegalStateException("Sesión inválida o expirada");
        }

        Persona persona = sesion.getPersona();  // la persona autenticada en esta sesión

        if (persona instanceof Doctor d)    return d.getUsuario();
        if (persona instanceof Enfermera e) return e.getUsuario();

        throw new IllegalStateException("Tipo de personal no reconocido: " + persona.getClass().getName());
    }
}
