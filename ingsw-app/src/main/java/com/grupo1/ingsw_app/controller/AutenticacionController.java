package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.Usuario;
import com.grupo1.ingsw_app.service.AutenticacionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class AutenticacionController {
    private final AutenticacionService auth;

    public AutenticacionController(AutenticacionService auth) { this.auth = auth; }

    // Para demo: sembramos usuarios al iniciar la app (constructor o @PostConstruct)
    // auth.register("delvallem","contr123","20-30574930-4", Set.of("ADMIN"));
    // auth.register("juareze","contr456","20-12547856-4", Set.of("USER"));

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password){
        String sessionId = auth.login(username, password);

        ResponseCookie cookie = ResponseCookie.from("SESSION_ID", sessionId)
                .httpOnly(true)
                .secure(true)              // true en HTTPS; en local podés desactivarlo si no usás https
                .sameSite("Strict")        // o "Lax" si necesitás navegación inter-sitio controlada
                .path("/")
                .maxAge(2 * 60 * 60)       // 2 horas
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("ok");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name="SESSION_ID", required=false) String sid){
        if (sid != null) auth.logout(sid);

        ResponseCookie expired = ResponseCookie.from("SESSION_ID", "")
                .httpOnly(true).secure(true).sameSite("Strict").path("/").maxAge(0).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expired.toString())
                .body("bye");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(name="SESSION_ID") String sid){
        Usuario u = auth.requireSession(sid);
        return ResponseEntity.ok(new MeDto(u.getUsuario()));
    }

    record MeDto(String username) {}
}
