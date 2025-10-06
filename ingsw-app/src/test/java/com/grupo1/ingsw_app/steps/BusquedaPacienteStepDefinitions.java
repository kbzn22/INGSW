package com.grupo1.ingsw_app.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.service.BuscadorPacientes;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class BusquedaPacienteStepDefinitions {

    private final IPacienteRepository repo = new PacienteRepository();
    private final BuscadorPacientes buscador = new BuscadorPacientes(repo);

    private boolean sesionValida;
    private String dniABuscar;
    private Paciente pacienteEncontrado;
    private String mensajeError;
    private Map<String, Paciente> pacientesEsperados = new HashMap<>();

    // ---------- GIVEN ----------
    @Given("la enfermera está autenticada en el sistema")
    public void la_enfermera_esta_autenticada_en_el_sistema() {
        sesionValida = true;
    }

    @Given("los pacientes existen en el sistema:")
    public void los_pacientes_existen_en_el_sistema(DataTable dataTable) {
        List<Map<String, String>> filas = dataTable.asMaps(String.class, String.class);

        List<Paciente> pacientes = filas.stream()
                .map(fila -> new Paciente(
                        fila.get("dni").trim(),
                        fila.get("nombre").trim()
                ))
                .collect(Collectors.toList());

        // guardamos los pacientes en el repo (y también para asserts)
        repo.clear();
        pacientes.forEach(repo::save);
        pacientesEsperados = pacientes.stream()
                .collect(Collectors.toMap(Paciente::getDni, p -> p));
    }

    // ---------- WHEN ----------
    @When("busco el paciente con dni {string}")
    public void busco_el_paciente_con_dni(String dni) {
        this.dniABuscar = dni;
        try {
            pacienteEncontrado = buscador.buscarPorDni(dni);
            mensajeError = null;
        } catch (Exception e) {
            pacienteEncontrado = null;
            mensajeError = e.getMessage();
        }
    }

    // ---------- THEN (caso éxito) ----------
    @Then("el sistema me muestra el paciente:")
    public void el_sistema_me_muestra_el_paciente(DataTable dataTable) {
        assertThat(pacienteEncontrado)
                .as("Debe existir un paciente encontrado para el DNI %s", dniABuscar)
                .isNotNull();

        // buscamos el paciente esperado según el DNI
        Map<String, String> fila = dataTable.asMaps(String.class, String.class).get(0);
        String dniEsperado = fila.get("dni").trim();
        Paciente esperado = pacientesEsperados.get(dniEsperado);

        // comparación completa
        assertThat(pacienteEncontrado)
                .as("El paciente retornado debe coincidir con el esperado en todos sus campos")
                .isEqualTo(esperado);
    }

    // ---------- THEN (caso error) ----------
    @Then("el sistema muestra un mensaje de error {string}")
    public void el_sistema_muestra_un_mensaje_de_error(String mensajeEsperado) {
        assertThat(mensajeError)
                .as("Se esperaba un mensaje de error específico")
                .isEqualTo(mensajeEsperado);
    }
}
