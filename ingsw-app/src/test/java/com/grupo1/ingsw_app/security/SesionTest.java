// src/test/java/com/grupo1/ingsw_app/security/SesionTest.java
package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.domain.Usuario;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

class SesionTest {

    private static final Pattern BASE64URL_43 = Pattern.compile("^[A-Za-z0-9_-]{43}$");

    private static Doctor doc(String username) {
        return new Doctor( "Maria", "Del Valle","20-30574930-4",  "maria@hospi.com","ABC123",
                new Usuario(username, "$2a$10$hash_simbolico"));
    }

    private static Enfermera enf(String username) {
        return new Enfermera("20-12547856-4", "Enzo", "Juarez", "ABC124", "enzo@hospi.com",
                new Usuario(username, "$2a$10$hash_simbolico"));
    }

    @Test
    @DisplayName("iniciar() establece id, usuario, persona y expiración futura (horas)")
    void cuandoInicioSesion_DeberiaEstablecerIDUsuarioPersonaYSesionTime() {
        Sesion s = new Sesion();
        Instant before = Instant.now();
        s.iniciar("delvallem", doc("delvallem"), 2L);
        Instant after  = Instant.now();

        assertThat(s.getId()).isNotBlank();
        assertThat(BASE64URL_43.matcher(s.getId()).matches())
                .as("id debe ser base64url (43 chars sin padding)").isTrue();

        assertThat(s.getUsuario()).isEqualTo("delvallem");
        assertThat(s.getPersona()).isInstanceOf(Doctor.class);


        Duration tol = Duration.ofSeconds(5);
        Instant min = before.plus(Duration.ofHours(2)).minus(tol);
        Instant max = after .plus(Duration.ofHours(2)).plus(tol);
        assertThat(s.getExpiresAt()).isBetween(min, max);

        assertThat(s.isExpired()).isFalse();
    }

    @Test
    @DisplayName("limpiar() deja la sesión vacía y isExpired() = true")
    void cuandoLimpioSesion_DeberiaQuedarSesionVacia() {
        Sesion s = new Sesion();
        s.iniciar("juareze", enf("juareze"), 1L);

        s.limpiar();

        assertThat(s.getId()).isNull();
        assertThat(s.getUsuario()).isNull();
        assertThat(s.getExpiresAt()).isNull();
        assertThatThrownBy(s::getPersona)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay persona autenticada");
        assertThat(s.isExpired()).isTrue();
    }

    @Test
    @DisplayName("isExpired() true cuando expiresAt es null o ya pasó")
    void isExpired_null_o_pasado() {
        Sesion s = new Sesion();

        // recièn creada sin iniciar -> null
        assertThat(s.isExpired()).isTrue();

        // forzamos expiración configurando una sesión corta y simulando pasada de tiempo:
        s.iniciar("x", enf("x"), -1L); // ahora + 0h
        // expiresAt ≈ now, cualquier retardo hará que sea true
        assertThat(s.isExpired()).isTrue();
    }

    @Test
    @DisplayName("setUsuario(Enfermera) extrae username, genera id y fija vencimiento 2h")
    void cuandoSeteoEnfermera_DeberiaGenerarIdYTiempoDeSesion() {
        Sesion s = new Sesion();
        Instant before = Instant.now();
        s.setUsuario(enf("juareze"));
        Instant after  = Instant.now();

        assertThat(s.getUsuario()).isEqualTo("juareze");
        assertThat(s.getPersona()).isInstanceOf(Enfermera.class);
        assertThat(s.getId()).isNotBlank();
        assertThat(BASE64URL_43.matcher(s.getId()).matches()).isTrue();

        // expiresAt ≈ now + 2h (tolerancia 5s)
        Duration tol = Duration.ofSeconds(5);
        Instant min = before.plus(Duration.ofHours(2)).minus(tol);
        Instant max = after .plus(Duration.ofHours(2)).plus(tol);
        assertThat(s.getExpiresAt()).isBetween(min, max);
    }

    @Test
    @DisplayName("setUsuario(Doctor) extrae username, genera id y fija vencimiento 2h")
    void cuandoSeteoDoctor_DeberiaGenerarIdYTiempoDeSesion() {
        Sesion s = new Sesion();
        s.setUsuario(doc("delvallem"));

        assertThat(s.getUsuario()).isEqualTo("delvallem");
        assertThat(s.getPersona()).isInstanceOf(Doctor.class);
        assertThat(BASE64URL_43.matcher(s.getId()).matches()).isTrue();
        assertThat(s.isExpired()).isFalse();
    }

    @Test
    @DisplayName("setUsuario(null) lanza IllegalArgumentException")
    void cuandoElUsuarioSeteadoEsNull_DeberiaLanzarExepcion() {
        Sesion s = new Sesion();
        assertThatThrownBy(() -> s.setUsuario(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede asignar null");
    }

    @Test
    @DisplayName("setUsuario(Persona sin Usuario embebido) lanza IllegalStateException")
    void cuandoLaPersonaNoTieneUsuario_DeberiaTirarExcepcion() {
        Sesion s = new Sesion();

        // Persona anónima (subtipo) sin usuario embebido
        Persona sinUsuario = new Persona("20-00000000-0", "Foo", "Bar", "foo@bar.com") {};
        assertThatThrownBy(() -> s.setUsuario(sinUsuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no tiene Usuario asociado");
    }

    @Test
    @DisplayName("getEnfermera() devuelve la enfermera si la sesión es de enfermería")
    void cuandoLaSesionEsDeUnaEnfermera_YSolicitoEnfermera_DeberiaDevolverLaEnfermera(){
        Sesion s = new Sesion();
        s.setUsuario(enf("juareze"));

        assertThat(s.getEnfermera())
                .isNotNull()
                .extracting(e -> e.getUsuario().getUsuario())
                .isEqualTo("juareze");
    }

    @Test
    @DisplayName("getEnfermera() en sesión de Doctor lanza SecurityException (rol requerido)")
    void cuandoLaSesionEsDeUnDoctor_YSolicitoEnfermera_DeberiaTirarExcepcion() {
        Sesion s = new Sesion();
        s.setUsuario(doc("delvallem"));

        assertThatThrownBy(s::getEnfermera)
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Se requiere rol ENFERMERIA");
    }

    @Test
    @DisplayName("getPersona() sin autenticar lanza IllegalStateException explícita")
    void cuandoSolicitoPersonaSinAutenticar_DeberiaLanzarExpecion() {
        Sesion s = new Sesion();
        assertThatThrownBy(s::getPersona)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay persona autenticada");
    }

    @Test
    @DisplayName("id generado es base64url (43 chars) y cambia en llamadas sucesivas")
    void id_formato_y_unicidad_basica() {
        Sesion s = new Sesion();
        s.iniciar("u1", enf("u1"), 1L);
        String id1 = s.getId();

        s.limpiar();
        s.iniciar("u2", doc("u2"), 1L);
        String id2 = s.getId();

        assertThat(id1).isNotEqualTo(id2);
        assertThat(BASE64URL_43.matcher(id1).matches()).isTrue();
        assertThat(BASE64URL_43.matcher(id2).matches()).isTrue();
    }
}
