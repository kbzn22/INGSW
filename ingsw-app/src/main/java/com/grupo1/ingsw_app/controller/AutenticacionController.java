package com.grupo1.ingsw_app.controller;


import com.grupo1.ingsw_app.service.AutenticacionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AutenticacionController {
    private final AutenticacionService auth;

    public AutenticacionController(AutenticacionService auth) { this.auth = auth; }

    // Para demo: sembramos usuarios al iniciar la app (constructor o @PostConstruct)
    // auth.register("delvallem","contr123","20-30574930-4", Set.of("ADMIN"));
    // auth.register("juareze","contr456","20-12547856-4", Set.of("USER"));

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

    public record LoginReq(String username, String password) {}
}


