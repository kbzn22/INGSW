package com.grupo1.ingsw_app.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grupo1.ingsw_app.domain.Paciente;

import java.util.Map;

public class BusquedaPacienteStepDefinitions extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbc;

    private ResponseEntity<Paciente> responsePaciente;
    private ResponseEntity<String> responseError;
    private final ObjectMapper mapper = new ObjectMapper();

    @Given("la enfermera está autenticada en el sistema")
    public void la_enfermera_esta_autenticada_en_el_sistema() {
        // No hace nada por ahora
    }

    @Given("los pacientes existen en el sistema:")
    public void los_pacientes_existen_en_el_sistema(DataTable dataTable) {
        System.out.println(">>> INSERTANDO PACIENTES");
        dataTable.asMaps().forEach(fila -> {
            jdbc.update("""
                INSERT INTO paciente (cuil, nombre, apellido, calle, numero, localidad)
                VALUES (?, ?, null, null, null, null)
            """, fila.get("cuil"), fila.get("nombre"));
        });
    }


    @When("busco el paciente con cuil {string}")
    public void busco_el_paciente_con_cuil(String cuil) {
        String url = "http://localhost:" + port + "/api/pacientes/" + cuil;

        ResponseEntity<String> rawResponse =
                restTemplate.getForEntity(url, String.class);

        try {
            if (rawResponse.getStatusCode().is2xxSuccessful()) {
                Paciente paciente = mapper.readValue(rawResponse.getBody(), Paciente.class);
                responsePaciente = ResponseEntity.ok(paciente);
                responseError = null;
            } else {
                responseError = rawResponse;
                responsePaciente = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error procesando respuesta JSON", e);
        }
    }


    @Then("el sistema me muestra el paciente:")
    public void el_sistema_me_muestra_el_paciente(DataTable dataTable) {

        Map<String, String> fila = dataTable.asMaps().get(0);
        Paciente esperado = new Paciente(fila.get("cuil"), fila.get("nombre"));

        assertThat(responsePaciente.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(responsePaciente.getBody());

        Paciente encontrado = responsePaciente.getBody();

        assertThat(encontrado.getCuil()).isEqualTo(esperado.getCuil());
        assertThat(encontrado.getNombre()).isEqualTo(esperado.getNombre());
    }


    @Then("el sistema muestra mensaje de error {string}")
    public void el_sistema_muestra_mensaje_error(String mensajeEsperado) {

        assertNotNull(responseError);
        assertTrue(responseError.getStatusCode().is4xxClientError());

        String cuerpo = responseError.getBody();
        assertTrue(cuerpo.contains(mensajeEsperado));
    }
}
