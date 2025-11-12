package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ObraSocial;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.dtos.PacienteRequest;
import com.grupo1.ingsw_app.exception.AfiliacionInvalidaException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.external.IObraSocialClient;
import com.grupo1.ingsw_app.persistence.IPacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PacienteServiceTest {

    @Mock
    private IPacienteRepository repoMock;

    @Mock
    private IObraSocialClient obraSocialClientMock;

    @InjectMocks
    private PacienteService pacienteService;

    // ---------- buscarPorCuil ----------
    @Test
    public void SiBuscoPorCuilAUnPacienteExistente_DeberiaDevolverlo(){
        //Arrenge
        String cuilBusqueda = "20-12345678-9";
        Paciente pacienteEsperado = new Paciente(
                cuilBusqueda,
                "Juan",
                "Pérez",
                "mail@mail.com",
                "Calle Falsa",
                123,
                "Tucumán",
                null,
                null
        );
        when(repoMock.findByCuil(cuilBusqueda)).thenReturn(Optional.of(pacienteEsperado));

        // Act
        Paciente pacienteResultado = pacienteService.buscarPorCuil(cuilBusqueda);

        //Assert
        assertEquals(pacienteEsperado, pacienteResultado);
        verify(repoMock, times(1)).findByCuil(cuilBusqueda);
    }

    @Test
    public void SiBuscoPorCuilAUnPacienteInexistente_DeberiaTirarUnaExcepcion(){
        //Arrenge
        String cuilBusqueda = "20-12345678-9";

        when(repoMock.findByCuil(cuilBusqueda)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                EntidadNoEncontradaException.class,
                () -> pacienteService.buscarPorCuil(cuilBusqueda)
        );
        verify(repoMock, times(1)).findByCuil(cuilBusqueda);
    }

    // ---------- registrarPaciente ----------
    @Test
    public void SiRegistroPacienteAfiliado_DeberiaGuardarseCorrectamente() {
        //Arrange
        UUID idObraSocial = UUID.randomUUID();
        String numeroAfiliado = "55555";
        PacienteRequest pacienteAguardar = new PacienteRequest(
                "20-12345678-9", "Pedro", "López", "pedro@mail.com",
                "Belgrano", 321, "Tucumán",
                idObraSocial, numeroAfiliado
        );

        ObraSocial obraSocial = new ObraSocial(idObraSocial, "Swiss Medical");
        when(obraSocialClientMock.buscarPorId(idObraSocial)).thenReturn(obraSocial);
        when(obraSocialClientMock.estaAfiliado(idObraSocial, numeroAfiliado)).thenReturn(true);

        doNothing().when(repoMock).save(any(Paciente.class));

        //Act
        Paciente pacienteGuardado = pacienteService.registrarPaciente(pacienteAguardar);

        //Assert
        assertNotNull(pacienteGuardado);
        assertEquals("Swiss Medical", pacienteGuardado.getAfiliado().getObraSocial().getNombre());
        verify(obraSocialClientMock).buscarPorId(idObraSocial);
        verify(obraSocialClientMock).estaAfiliado(idObraSocial, numeroAfiliado);
        verify(repoMock, times(1)).save(any(Paciente.class));
    }

    @Test
    public void SiRegistroUnPacienteSinObraSocial_DeberiaGuardarseCorrectamente() {
        // Arrange
        PacienteRequest pacienteAGuardar = new PacienteRequest(
                "20-12345678-9",
                "Juan",
                "Pérez",
                "juanperez@mail.com",
                "Av. Siempre Viva",
                742,
                "San Miguel de Tucumán",
                null,
                null
        );

        // Mockeamos la operación save para que devuelva el mismo objeto
        doAnswer(invocation -> {
            Paciente p = invocation.getArgument(0);
            return p;
        }).when(repoMock).save(any(Paciente.class));

        // Act
        Paciente pacienteGuardado = pacienteService.registrarPaciente(pacienteAGuardar);

        // Assert
        assertNotNull(pacienteGuardado);
        assertEquals(pacienteAGuardar.getCuil(), pacienteGuardado.getCuil().getValor(),
                "El CUIL del paciente guardado debe coincidir con el del request");
        verify(repoMock, times(1)).save(any(Paciente.class));
    }

    @Test
    public void SiRegistroPacienteConObraSocialInexistente_DeberiaLanzarExcepcion() {
        //Arrange
        UUID idObraSocial = UUID.randomUUID();
        String numeroAfiliado = "12345";
        PacienteRequest pacienteAguardar = new PacienteRequest(
                "20-98765432-1", "María", "Gómez", "maria@mail.com",
                "Rivadavia", 800, "Yerba Buena",
                idObraSocial, numeroAfiliado
        );

        // simulamos que la obra social no existe en el sistema
        when(obraSocialClientMock.buscarPorId(idObraSocial)).thenReturn(null);

        //Act & Assert
        assertThrows(EntidadNoEncontradaException.class,
                () -> pacienteService.registrarPaciente(pacienteAguardar));

        //Verificamos que el metodo fue invocado y que NO se guardó nada
        verify(obraSocialClientMock, times(1)).buscarPorId(idObraSocial);
        verify(obraSocialClientMock, never()).estaAfiliado(any(), any());
        verify(repoMock, never()).save(any());
    }


    @Test
    public void SiRegistroPacienteNoAfiliado_DeberiaLanzarExcepcion() {
        //Arrange
        UUID idObraSocial = UUID.randomUUID();
        String numeroAfiliado = "99999";
        PacienteRequest req = new PacienteRequest(
                "20-12345678-9", "Luis", "Paz", "luis@mail.com",
                "Mitre", 200, "Tafí Viejo",
                idObraSocial, numeroAfiliado
        );

        ObraSocial obraSocial = new ObraSocial(idObraSocial, "OSDE");
        when(obraSocialClientMock.buscarPorId(idObraSocial)).thenReturn(obraSocial);
        when(obraSocialClientMock.estaAfiliado(idObraSocial, numeroAfiliado)).thenReturn(false);

        //Act & Assert
        assertThrows(AfiliacionInvalidaException.class,
                () -> pacienteService.registrarPaciente(req));

        verify(obraSocialClientMock, times(1)).buscarPorId(idObraSocial);
        verify(obraSocialClientMock, times(1)).estaAfiliado(idObraSocial, numeroAfiliado);
        verify(repoMock, never()).save(any());
    }


}
