package com.grupo1.ingsw_app.unitTesting;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Usuario;
import com.grupo1.ingsw_app.persistence.PersonalRepository;
import com.grupo1.ingsw_app.persistence.SesionRepository;
import com.grupo1.ingsw_app.security.Sesion;
import com.grupo1.ingsw_app.service.AutenticacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class WhenAutenticacionTest {

    private PasswordEncoder encoder;
    private Sesion sesion;
    private PersonalRepository personal;
    private AutenticacionService auth;

    @BeforeEach
    void setup() {
        encoder   = new BCryptPasswordEncoder();
        sesion  = new Sesion();
        personal  = new PersonalRepository();
        auth      = new AutenticacionService(personal,sesion, encoder);

        Usuario uDoc = new Usuario("delvallem", encoder.encode("contr123"));
        Usuario uEnf = new Usuario("juareze", encoder.encode("contr456"));

        Doctor doctora = new Doctor("Maria","Del Valle", "20-30574930-4", "ABC123", "maria@hospi.com", uDoc);
        Enfermera enfermero = new Enfermera("20-12547856-4", "Enzo", "Juarez", "ABC124", "enzo@hospi.com", uEnf);

        personal.save(doctora);
        personal.save(enfermero);
    }

    @Test
    @DisplayName("Login correcto devuelve sessionId y permite obtener Usuario con requireSession")
    void login_ok_y_require_session() {
        String sid = auth.login("delvallem", "contr123");

        assertThat(sid).isNotBlank();

        Usuario u = auth.requireSession(sid);
        assertThat(u.getUsuario()).isEqualTo("delvallem");
    }

    @Test
    @DisplayName("Usuario inexistente lanza NoSuchElementException")
    void usuario_inexistente() {
        assertThatThrownBy(() -> auth.login("lizarragaj", "contr123"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("El usuario no existe");
    }

    @Test
    @DisplayName("Contraseña incorrecta lanza IllegalArgumentException")
    void contrasena_incorrecta() {
        assertThatThrownBy(() -> auth.login("delvallem", "contr999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña es incorrecta");
    }

    @Test
    @DisplayName("requireSession falla si la sesión no existe")
    void require_session_invalida() {
        assertThatThrownBy(() -> auth.requireSession("SID_INEXISTENTE"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Sesión inválida o expirada");
    }
}

