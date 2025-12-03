package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.security.Sesion;
import com.grupo1.ingsw_app.service.AutenticacionService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AutenticacionController {
    private final AutenticacionService auth;

    public AutenticacionController(AutenticacionService auth) { this.auth = auth; }

    @PostMapping("/login")
    public ResponseEntity<Void> loginJson(@RequestBody LoginReq req) {
        String sid = auth.login(req.username(), req.password());
        ResponseCookie cookie = ResponseCookie.from("SESSION_ID", sid)
                .httpOnly(true).secure(false) // poné true si usás HTTPS
                .sameSite("Lax").path("/").build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    // acepta form-url-encoded: username=..&password=..
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> loginForm(@RequestParam String username, @RequestParam String password) {
        return loginJson(new LoginReq(username, password));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "SESSION_ID", required = false) String sid) {

        // aunque sid sea null, nuestro service ahora lo tolera
        auth.logout(sid);

        // armamos cookie expirada para borrar en navegador
        ResponseCookie cookie = ResponseCookie.from("SESSION_ID", "")
                .httpOnly(true)
                .secure(false) // true si usás HTTPS
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
    @GetMapping("/me")
    public ResponseEntity<?> me(
            @CookieValue(name = "SESSION_ID", required = false) String sid) {

        if (sid == null || sid.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {

            Sesion sesion = auth.requireSesionCompleta(sid);
            Persona persona = sesion.getPersona();

            String rol;
            String nombre = null;
            String apellido = null;
            String cuil = null;

            if (persona instanceof Doctor d) {
                rol = "DOCTOR";
                nombre = d.getNombre();
                apellido = d.getApellido();
                cuil = d.getCuil().getValor();
            } else if (persona instanceof Enfermera e) {
                rol = "ENFERMERA";
                nombre = e.getNombre();
                apellido = e.getApellido();
                cuil = e.getCuil().getValor();
            } else {
                rol = "DESCONOCIDO";
            }

            var dto = Map.of(
                    "username", sesion.getUsuario(),
                    "rol", rol,
                    "nombre", nombre,
                    "apellido", apellido,
                    "cuil", cuil
            );

            return ResponseEntity.ok(dto);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/verificar")
    public ResponseEntity<Void> verificar(
            @CookieValue(name = "SESSION_ID", required = false) String sid) {

        if (sid == null || sid.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Podés usar requireSession o requireSesionCompleta, da igual mientras valide
            auth.requireSesionCompleta(sid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    public record LoginReq(String username, String password) {}
}


