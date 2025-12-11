package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.security.Sesion;
import com.grupo1.ingsw_app.service.AutenticacionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AutenticacionController {

    private final AutenticacionService auth;

    public AutenticacionController(AutenticacionService auth) {
        this.auth = auth;
    }

    // ---------- LOGIN JSON ----------
    @PostMapping("/login")
    public ResponseEntity<Void> loginJson(
            @RequestBody LoginReq req,
            HttpServletRequest request
    ) {
        String sid = auth.login(req.username(), req.password());

        boolean isLocalhost = isLocalRequest(request);

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from("SESSION_ID", sid)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(8));

        if (isLocalhost) {
            // Dev local: http://localhost:8080
            builder.secure(false)
                    .sameSite("Lax");
        } else {
            // Prod: Railway (HTTPS, front en otro dominio)
            builder.secure(true)
                    .sameSite("None");
        }

        ResponseCookie cookie = builder.build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // acepta form-url-encoded: username=..&password=..
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> loginForm(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request
    ) {
        return loginJson(new LoginReq(username, password), request);
    }

    // ---------- LOGOUT ----------
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "SESSION_ID", required = false) String sid,
            HttpServletRequest request
    ) {
        auth.logout(sid);

        boolean isLocalhost = isLocalRequest(request);

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from("SESSION_ID", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0); // borrar cookie

        if (isLocalhost) {
            builder.secure(false)
                    .sameSite("Lax");
        } else {
            builder.secure(true)
                    .sameSite("None");
        }

        ResponseCookie cookie = builder.build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // ---------- ME ----------
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

    // ---------- VERIFICAR ----------
    @GetMapping("/verificar")
    public ResponseEntity<Void> verificar(
            @CookieValue(name = "SESSION_ID", required = false) String sid) {

        if (sid == null || sid.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            auth.requireSesionCompleta(sid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private boolean isLocalRequest(HttpServletRequest request) {
        String host = request.getServerName();
        return "localhost".equals(host) || "127.0.0.1".equals(host);
    }

    public record LoginReq(String username, String password) {}
}
