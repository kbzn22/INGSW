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
    @Test
    @DisplayName("login OK: elimina sesiones previas del usuario, inicia y persiste nueva sesión")
    void cuandoinicioSesionYYaexistessesionconmiusuario_deberiaeliminarlaotrasesionyempezarunanueva() {

        var d = doctor("delvallem", "{bcrypt}hash");
        when(personalRepo.findByUsername("delvallem")).thenReturn(Optional.of(d));
        when(encoder.matches("contr123", d.getUsuario().getPassword())).thenReturn(true);
        when(sesion.getId()).thenReturn("SID123");


        String sid = auth.login("delvallem", "contr123");


        assertThat(sid).isEqualTo("SID123");

        InOrder in = inOrder(personalRepo, encoder, sesionRepo, sesion);
        in.verify(personalRepo).findByUsername("delvallem");
        in.verify(encoder).matches("contr123", d.getUsuario().getPassword());

        in.verify(sesionRepo).deleteByPersona(d.getCuil().getValor());

        in.verify(sesion).iniciar("delvallem", d, 2L);

        in.verify(sesionRepo).save(sesion);
        in.verify(sesion).getId();
        in.verifyNoMoreInteractions();
    }
    @Test
    @DisplayName("login dos veces: cada login limpia sesiones previas del mismo usuario antes de crear la nueva")
    void cuandoInicioSesion_noDeberiaHaberOtraSesionConMiCuil() {
        var d = doctor("delvallem", "{bcrypt}hash");
        when(personalRepo.findByUsername("delvallem")).thenReturn(Optional.of(d));
        when(encoder.matches("contr123", d.getUsuario().getPassword())).thenReturn(true);

        when(sesion.getId()).thenReturn("SID1", "SID2");


        String sid1 = auth.login("delvallem", "contr123");
        String sid2 = auth.login("delvallem", "contr123");


        assertThat(sid1).isEqualTo("SID1");
        assertThat(sid2).isEqualTo("SID2");


        verify(sesionRepo, times(2)).deleteByPersona(d.getCuil().getValor());
        verify(sesion,       times(2)).iniciar("delvallem", d, 2L);
        verify(sesionRepo,   times(2)).save(sesion);
        verify(sesion,       times(2)).getId();
        verifyNoMoreInteractions(personalRepo, encoder, sesionRepo, sesion);
    }

    @Test
    @DisplayName("logout OK: elimina la sesión en DB y limpia la sesión actual cuando el SID coincide")
    void cuandoHagoLogout_deberiaEliminarSesiondeDByBorrarSID() {
        // la sesión actual tiene este SID
        when(sesion.getId()).thenReturn("SID123");

        // when
        auth.logout("SID123");

        // then
        InOrder in = inOrder(sesionRepo, sesion);
        // primero borrar en DB por SID
        in.verify(sesionRepo).delete("SID123");
        // luego consultamos el id actual
        in.verify(sesion).getId();
        // como coincide, limpiamos la sesión en memoria
        in.verify(sesion).limpiar();
        in.verifyNoMoreInteractions();
    }
    @Test
    @DisplayName("logout con SID distinto: elimina en DB pero no limpia la sesión actual")
    void logout_sid_distinto_elimina_en_db_pero_no_limpia_sesion_actual() {
        when(sesion.getId()).thenReturn("SID_ACTUAL");

        auth.logout("SID_OTRO");


        verify(sesionRepo).delete("SID_OTRO");

        verify(sesion).getId();

        verify(sesion, never()).limpiar();
        verifyNoMoreInteractions(sesionRepo, sesion);
    }
    @Test
    @DisplayName("logout usuario A no afecta a la sesión del usuario B")
    void logout_de_un_usuario_no_elimina_la_sesion_de_otro() {

        // --- Usuario A ---
        var dA = doctor("delvallem", "{bcrypt}hashA");
        when(personalRepo.findByUsername("delvallem")).thenReturn(Optional.of(dA));
        when(encoder.matches("passA", dA.getUsuario().getPassword())).thenReturn(true);

        // cuando se llame al getId() por sesión de A
        when(sesion.getId()).thenReturn("SID_A");

        // login A
        String sidA = auth.login("delvallem", "passA");
        assertThat(sidA).isEqualTo("SID_A");

        // --- Usuario B ---
        var dB = doctor("juareze", "{bcrypt}hashB");
        when(personalRepo.findByUsername("juareze")).thenReturn(Optional.of(dB));
        when(encoder.matches("passB", dB.getUsuario().getPassword())).thenReturn(true);

        // segunda sesión: nuevo SID
        when(sesion.getId()).thenReturn("SID_B");

        // login B
        String sidB = auth.login("juareze", "passB");
        assertThat(sidB).isEqualTo("SID_B");


        // --- LOGOUT de usuario A ---
        // cuando logout consulte getId() debe devolver SID_A para matchear logout
        when(sesion.getId()).thenReturn("SID_A");

        auth.logout("SID_A");


        // --- Verificaciones ---
        // Se debe borrar SOLO la sesión de A
        verify(sesionRepo).delete("SID_A");

        // NUNCA se debe intentar borrar la sesión de B
        verify(sesionRepo, never()).delete("SID_B");

        // Sesión de A debe limpiarse
        verify(sesion).limpiar();

        // Sesión de B NO debe ser tocada
        // No hacemos verify sobre "sesion de B" porque el servicio usa un único sesion mock,
        // pero controlamos que ningún



    }}
