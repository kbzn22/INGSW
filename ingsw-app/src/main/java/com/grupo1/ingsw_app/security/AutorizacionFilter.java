
package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.service.AutenticacionService;
import com.grupo1.ingsw_app.persistence.IPersonalRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class AutorizacionFilter extends OncePerRequestFilter {

    private final AutenticacionService auth;
    private final IPersonalRepository personal;
    private final Sesion sesionActual;

    public AutorizacionFilter(AutenticacionService auth, IPersonalRepository personal, Sesion sesionActual) {
        this.auth = auth; this.personal = personal; this.sesionActual = sesionActual;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String sid = readCookie(req, "SESSION_ID");
        if (sid != null) {
            try {
                var usuario = auth.requireSession(sid);
                var persona = personal.findByUsername(usuario.getUsuario()).orElse(null);
                if (persona != null) sesionActual.setUsuario(persona);
            } catch (Exception ignored) {

            }
        }
        chain.doFilter(req, res);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/auth/login");
    }


    private static String readCookie(HttpServletRequest req, String name) {
        var cs = req.getCookies();
        if (cs == null) return null;
        return Arrays.stream(cs)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
