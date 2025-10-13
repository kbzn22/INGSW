package com.grupo1.ingsw_app.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.http.HttpStatus;

import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

// Esta clase hereda de la configuración que integra Cucumber con Spring Boot
public class BusquedaPacienteStepDefinitions extends CucumberSpringConfiguration {

    // Puerto aleatorio asignado al servidor embebido de Spring Boot
    @LocalServerPort
    private int port;

    // Cliente HTTP de pruebas (proveído por Spring Boot Test)
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IPacienteRepository repo;

    // Variables para almacenar las respuestas HTTP
    private ResponseEntity<Paciente> responsePaciente; // respuesta exitosa
    private ResponseEntity<String> responseError;      // respuesta de error

    @Given("la enfermera está autenticada en el sistema")
    public void la_enfermera_esta_autenticada_en_el_sistema() {
    }

    @Given("los pacientes existen en el sistema:")
    public void los_pacientes_existen_en_el_sistema(DataTable dataTable) {
        repo.clear(); // limpiamos datos previos si existieran
        dataTable.asMaps().forEach(fila -> {
            Paciente paciente = new Paciente(fila.get("cuil"), fila.get("nombre"));
            repo.save(paciente);
        });
    }

    @When("busco el paciente con cuil {string}")
    public void busco_el_paciente_con_cuil(String cuil) {
        String url = "http://localhost:" + port + "/api/pacientes/" + cuil;

        try {
            // Pedimos SIEMPRE la respuesta como String
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);

            // Si es 200, parseamos el JSON manualmente
            if (rawResponse.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                Paciente paciente = mapper.readValue(rawResponse.getBody(), Paciente.class);
                responsePaciente = ResponseEntity.ok(paciente);
                responseError = null;
            } else {
                // Si es error, guardamos el texto
                responseError = rawResponse;
                responsePaciente = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error durante la llamada al endpoint", e);
        }
    }

    @Then("el sistema me muestra el paciente:")
    public void el_sistema_me_muestra_el_paciente(DataTable dataTable) {
        // Obtenemos el paciente esperado
        Map<String, String> fila = dataTable.asMaps(String.class, String.class).get(0);
        Paciente pacienteEsperado = new Paciente(fila.get("cuil"), fila.get("nombre"));

        assertThat(responsePaciente.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responsePaciente.getBody()).isNotNull();

        // Obtenemos el paciente devuelto por la API
        Paciente pacienteEncontrado = responsePaciente.getBody();

        assertThat(pacienteEncontrado.getCuil()).isEqualTo(pacienteEsperado.getCuil());
        assertThat(pacienteEncontrado.getNombre()).isEqualTo(pacienteEsperado.getNombre());

    }

    @Then("el sistema muestra un mensaje de error {string}")
    public void el_sistema_muestra_un_mensaje_de_error(String mensajeEsperado) {
        // Verificamos que haya una respuesta de error
        assertNotNull(responseError, "Se esperaba una respuesta de error pero fue nula");

        // El código de estado debe ser 400 o 404 (depende del caso)
        HttpStatusCode status = responseError.getStatusCode();
        assertTrue(status.is4xxClientError(),
                "El estado HTTP debería ser 4xx, pero fue: " + status);

        // El mensaje devuelto por la API debe contener el texto esperado
        String cuerpo = responseError.getBody();
        assertTrue(cuerpo.contains(mensajeEsperado),
                "El mensaje esperado era '" + mensajeEsperado + "' pero fue '" + cuerpo + "'");

    }

}
