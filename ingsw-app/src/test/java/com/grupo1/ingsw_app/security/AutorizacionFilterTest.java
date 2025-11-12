package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Usuario;
import com.grupo1.ingsw_app.persistence.IPersonalRepository;
import com.grupo1.ingsw_app.service.AutenticacionService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutorizacionFilterTest {

    @Mock AutenticacionService auth;
    @Mock IPersonalRepository personal;

    Sesion sesionActual;

    AutorizacionFilter filter;

    @BeforeEach
    void setup() {
        sesionActual = new Sesion(); // tu clase real (POJO)
        filter = new AutorizacionFilter(auth, personal, sesionActual);
    }

    @Test
    @DisplayName("No cookie: no setea usuario y deja pasar la request")
    void siNoExisteCookie_noDeberiaSetearUsuarioyDejaPasarReq() throws ServletException, IOException {
        var req = new MockHttpServletRequest("GET", "/api/ingresos");
        var res = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilter(req, res, chain);


        verify(auth, never()).requireSession(anyString());
        assertThat(assertPersonaNullable()).isNull();
    }

    @Test
    @DisplayName("Cookie con sesión válida: setea Sesion con la persona hallada")
    void siExisteCookie_DeberiaSetearUsuarioEnSesionyDejaPasarReq() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/ingresos");
        req.setCookies(new jakarta.servlet.http.Cookie("SESSION_ID", "abc"));
        var res = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        var usuario = new Usuario("delvallem", "hash");
        when(auth.requireSession("abc")).thenReturn(usuario);

        var enf = new Enfermera("20-12547856-4", "Enzo", "Juarez", "ABC124", "e@h.com", usuario);
        when(personal.findByUsername("delvallem")).thenReturn(Optional.of(enf));

        filter.doFilter(req, res, chain);


        var persona = sesionActual.getPersona();
        assertThat(persona).isSameAs(enf);
        verify(auth).requireSession("abc");
        verify(personal).findByUsername("delvallem");
    }

    @Test
    @DisplayName("Cookie con sesión inválida: no rompe y deja pasar la request")
    void siLaCookieEsInvalida_DeberiaCathcearLaExpecion() throws Exception {
        var req = new MockHttpServletRequest("GET", "/api/ingresos");
        req.setCookies(new jakarta.servlet.http.Cookie("SESSION_ID", "malo"));
        var res = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        when(auth.requireSession("malo")).thenThrow(new IllegalStateException("Sesión inválida o expirada"));


        filter.doFilter(req, res, chain);


        assertThat(assertPersonaNullable()).isNull();
        verify(auth).requireSession("malo");
        verify(personal, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("No filtra /auth/login (shouldNotFilter)")
    void cuandoIngresoPorLogin_NoDeberiaAplicarFiltro() throws Exception {
        var loginReq = new MockHttpServletRequest("POST", "/auth/login");
        var res = new MockHttpServletResponse();
        var chain = new MockFilterChain();


        boolean skip = filter.shouldNotFilter(loginReq);
        assertThat(skip).isTrue();

        filter.doFilter(loginReq, res, chain);
        verifyNoInteractions(auth, personal);
    }

    private Object assertPersonaNullable() {
        try {
            return sesionActual.getPersona();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
