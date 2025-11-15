package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.domain.Usuario;
import com.grupo1.ingsw_app.persistence.IPersonalRepository;
import com.grupo1.ingsw_app.persistence.ISesionRepository;
import com.grupo1.ingsw_app.security.Sesion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock private IPersonalRepository personalRepo;
    @Mock private Sesion sesion;
    @Mock private PasswordEncoder encoder;
    @Mock private ISesionRepository sesionRepo;

    @InjectMocks
    private AutenticacionService auth;

    // -------- helpers --------
    private Doctor doctor(String username, String rawPassHashed) {
        var u = new Usuario(username, rawPassHashed);
        return new Doctor("Maria", "Del Valle", "20-30574930-4",  "maria@hospi.com","ABC123", u);
    }
    private Enfermera enfermera(String username, String rawPassHashed) {
        var u = new Usuario(username, rawPassHashed);
        return new Enfermera("20-12547856-4", "Enzo", "Juarez", "ABC124", "enzo@hospi.com", u);
    }

    @Test
    @DisplayName("login OK: valida credenciales, inicia sesión y retorna sessionId")
    void login_ok() {
        var d = doctor("delvallem", "{bcrypt}hash"); // el valor es simbólico
        when(personalRepo.findByUsername("delvallem")).thenReturn(Optional.of(d));
        when(encoder.matches("contr123", d.getUsuario().getPassword())).thenReturn(true);


        when(sesion.getId()).thenReturn("SID123");

        String sid = auth.login("delvallem", "contr123");

        assertThat(sid).isEqualTo("SID123");


        InOrder in = inOrder(personalRepo, encoder, sesion, sesionRepo);
        in.verify(personalRepo).findByUsername("delvallem");
        in.verify(encoder).matches("contr123", d.getUsuario().getPassword());
        in.verify(sesion).iniciar(eq("delvallem"), eq(d), eq(2L));
        in.verify(sesionRepo).save(sesion);
        in.verify(sesion).getId();
        in.verifyNoMoreInteractions();

        in.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("login con usuario inexistente → NoSuchElementException")
    void login_usuario_inexistente() {
        when(personalRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auth.login("ghost", "x"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("El usuario no existe");

        verify(personalRepo).findByUsername("ghost");
        verifyNoMoreInteractions(personalRepo, sesion, encoder);
    }

    @Test
    @DisplayName("login con password incorrecta → IllegalArgumentException")
    void login_password_incorrecta() {
        var e = enfermera("juareze", "{bcrypt}hash");
        when(personalRepo.findByUsername("juareze")).thenReturn(Optional.of(e));
        when(encoder.matches("mal", e.getUsuario().getPassword())).thenReturn(false);

        assertThatThrownBy(() -> auth.login("juareze", "mal"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña es incorrecta");

        verify(personalRepo).findByUsername("juareze");
        verify(encoder).matches("mal", e.getUsuario().getPassword());
        verifyNoMoreInteractions(personalRepo, sesion, encoder);
    }

    @Test
    @DisplayName("requireSession OK: valida el SID vigente y retorna el Usuario")
    void require_session_ok() {
        // simulamos que ya hay una sesión con ese id y no está expirada
        when(sesion.getId()).thenReturn("SID_OK");
        when(sesion.isExpired()).thenReturn(false);

        // y que la persona guardada es una Enfermera con Usuario "juareze"
        var e = enfermera("juareze", "{bcrypt}");
        when(sesion.getPersona()).thenReturn(e);

        var u = auth.requireSession("SID_OK");

        assertThat(u.getUsuario()).isEqualTo("juareze");

        InOrder in = inOrder(sesion, sesionRepo);

        in.verify(sesion, times(3)).getId();
        in.verify(sesion).isExpired();
        in.verify(sesion).getPersona();
        in.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("requireSession con SID inválido/expirado → IllegalStateException")
    void require_session_invalida() {
        when(sesion.getId()).thenReturn("SID_REAL");


        assertThatThrownBy(() -> auth.requireSession("OTRO_SID"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Sesión inválida o expirada");
    }

    @Test
    @DisplayName("requireSession con persona de tipo desconocido → IllegalStateException explícita")
    void require_session_tipo_desconocido() {
        when(sesion.getId()).thenReturn("SID_X");
        when(sesion.isExpired()).thenReturn(false);

        // creamos un subtipo anónimo de Persona (sin Doctor/Enfermera) para forzar el error
        Persona rara = new Persona("20-00000000-0",
                "Foo", "Bar", "foo@bar.com") {};
        when(sesion.getPersona()).thenReturn(rara);

        assertThatThrownBy(() -> auth.requireSession("SID_X"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Tipo de personal no reconocido");
    }
}
