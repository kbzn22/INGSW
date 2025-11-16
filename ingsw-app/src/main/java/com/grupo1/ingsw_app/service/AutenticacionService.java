package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.persistence.IPersonalRepository;
import com.grupo1.ingsw_app.persistence.ISesionRepository;
import com.grupo1.ingsw_app.security.Sesion;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;

public class AutenticacionService {

    private final IPersonalRepository personalRepo;
    private final Sesion sesion;
    private final PasswordEncoder encoder;
    private final ISesionRepository sesionRepo;

    public AutenticacionService(IPersonalRepository personalRepo, Sesion sesion, PasswordEncoder encoder,ISesionRepository sesionRepo) {
        this.personalRepo = personalRepo;
        this.sesion = sesion;
        this.encoder = encoder;
        this.sesionRepo = sesionRepo;
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
        String cuilPersona = persona.getCuil().getValor();
        sesionRepo.deleteByPersona(cuilPersona);

        sesion.iniciar(cuenta.getUsuario(), persona, 2L); // 2 horas
        sesionRepo.save(sesion);
        return sesion.getId();
    }

    public Usuario requireSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalStateException("Sesión inválida o expirada");
        }

        // por si llega con comillas de algún lado
        String cleanId = sessionId.trim();
        if (cleanId.startsWith("\"") && cleanId.endsWith("\"") && cleanId.length() > 1) {
            cleanId = cleanId.substring(1, cleanId.length() - 1);
        }

        var sesionOpt = sesionRepo.find(cleanId);
        if (sesionOpt.isEmpty()) {
            throw new IllegalStateException("Sesión inválida o expirada");
        }

        Sesion s = sesionOpt.get();


        Persona persona = s.getPersona();

        if (persona instanceof Doctor d)    return d.getUsuario();
        if (persona instanceof Enfermera e) return e.getUsuario();

        throw new IllegalStateException("Tipo de personal no reconocido: " + persona.getClass().getName());
    }
    public void logout(String sid) {
        // Si no viene SID, no hacemos nada
        if (sid == null || sid.isBlank()) {
            return;
        }

        // 1) Siempre intento borrar en DB por SID
        try {
            sesionRepo.delete(sid);
        } catch (Exception e) {
            // si querés loguear, bien, pero no tiraría 500 por esto
            // log.warn("Error borrando sesión {}", sid, e);
        }

        // 2) Si la sesión en memoria coincide, la limpio
        String current = sesion.getId();
        if (current != null && current.equals(sid)) {
            sesion.limpiar();
        }
    }

}
