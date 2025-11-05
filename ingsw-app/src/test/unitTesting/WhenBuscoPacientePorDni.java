package com.grupo1.unitTesting;

import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.service.BuscadorPacientes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WhenBuscoPacientePorDniTest {

    private BuscadorPacientes nuevoBuscadorConDatos() {
        BuscadorPacientes buscador = new BuscadorPacientes();
        buscador.registrarPacientes(Arrays.asList(
                new Paciente("44477310", "enzo juarez"),
                new Paciente("43650619", "paula madrid"),
                new Paciente("12345678", "mirtha legrand")
        ));
        return buscador;
    }

    @Test
    @DisplayName("cuando ingreso un DNI existente, devuelve el paciente correcto")
    void buscarPacientePorDni_DniExiste_DeberiaRetornarPaciente() {
        BuscadorPacientes buscador = nuevoBuscadorConDatos();

        Paciente obtenido = buscador.buscarPorDni("44477310");

        assertThat(obtenido)
                .extracting(Paciente::getDni, Paciente::getNombre)
                .containsExactly("44477310", "enzo juarez");
    }

    @Test
    @DisplayName("cuando el DNI no existe, lanza 'Paciente no encontrado'")
    void buscarPacientePorDni_DniNoExiste_DeberiaLanzarNoSuchElement() {
        BuscadorPacientes buscador = nuevoBuscadorConDatos();

        assertThatThrownBy(() -> buscador.buscarPorDni("99999999"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Paciente no encontrado");
    }

    @Test
    @DisplayName("cuando el DNI es alfanumérico, lanza 'DNI inválido'")
    void buscarPacientePorDni_DniInvalido_DeberiaLanzarIllegalArgument() {
        BuscadorPacientes buscador = nuevoBuscadorConDatos();

        assertThatThrownBy(() -> buscador.buscarPorDni("ABC123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("DNI inválido");
    }
}
