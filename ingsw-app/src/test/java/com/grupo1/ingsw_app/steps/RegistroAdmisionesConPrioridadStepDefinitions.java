package com.grupo1.ingsw_app.steps;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import com.grupo1.ingsw_app.security.SesionActual;
import com.grupo1.ingsw_app.service.IngresoService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistroAdmisionesConPrioridadStepDefinitions extends CucumberSpringConfiguration {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IngresoService ingresoService;

    @Autowired
    private IPacienteRepository pacienteRepo;

    @Autowired
    private IIngresoRepository ingresoRepo;

    @Autowired
    private SesionActual sesionActual;

    private ResponseEntity<String> responseError;
    private ResponseEntity<Ingreso> responseIngreso;

    private Paciente pacienteActual;
    private Enfermera enfermeraActual;

    private final ObjectMapper mapper = new ObjectMapper();
    private final LocalDate fechaBase = LocalDate.of(2025, 10, 12);
    private ColaAtencion cola = new ColaAtencion();
    private int posicionResultante;

    private Ingreso ingresoActual;
    private Map<String, Object> ingresoJson;

    @Given("la siguiente enfermera está autenticada en el sistema")
    public void laEnfermeraSiguienteEnfermeraEstáAutenticadaEnElSistema(DataTable table) {
        Map<String, String> fila = table.asMaps(String.class, String.class).get(0);
        Enfermera enfermera = new Enfermera(
                fila.get("cuil"),
                fila.get("nombre"),
                fila.get("apellido"),
                fila.get("matricula"),
                "");
        sesionActual.setEnfermeraActual(enfermera);
        enfermeraActual = enfermera;
    }

    @And("existe en el sistema el paciente:")
    public void existeEnElSistemaElPaciente(DataTable dataTable) {
        pacienteRepo.clear();
        Map<String, String> fila = dataTable.asMaps(String.class, String.class).get(0);
        Paciente paciente = new Paciente(fila.get("cuil"), fila.get("nombre"));
        pacienteRepo.save(paciente);
        pacienteActual = paciente;
    }

    @When("registro el ingreso del paciente con los siguientes datos:")
    @When("intento registrar el ingreso del paciente con los siguientes datos:")
    public void registroElIngresoDelPacienteConLosSiguientesDatos(DataTable table) {

        Map<String, String> fila = table.asMaps(String.class, String.class).get(0);

        String url = "http://localhost:" + port + "/api/ingresos";

        java.util.function.Function<String, String> toNullableTrimmed =
                s -> {
                    if (s == null) return null;
                    String raw = s.trim();
                    if (raw.equalsIgnoreCase("null")) return null;
                    // si viene entre comillas ("...") se las saco
                    if (raw.length() >= 2 && raw.startsWith("\"") && raw.endsWith("\"")) {
                        raw = raw.substring(1, raw.length() - 1);
                    }
                    return raw;
                };

        java.util.function.Function<String, Object> numOrRaw = s -> {
            String v = toNullableTrimmed.apply(s);
            if (v == null) return null;                // null real
            try {
                if (v.contains(".")) return Double.parseDouble(v); // número con punto
                return Integer.parseInt(v);                        // entero
            } catch (NumberFormatException ex) {
                return v; // lo mando como String para que falle en el backend (400)
            }
        };
        // Construimos el cuerpo de la request como JSON
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("cuilPaciente", pacienteActual.getCuil().getValor());
        body.put("cuilEnfermera", enfermeraActual.getCuil().getValor());
        body.put("informe", toNullableTrimmed.apply(fila.get("informe")));
        body.put("temperatura", numOrRaw.apply(fila.get("temperatura")));
        body.put("frecuenciaCardiaca", numOrRaw.apply(fila.get("frecuencia cardiaca")));
        body.put("frecuenciaRespiratoria", numOrRaw.apply(fila.get("frecuencia respiratoria")));
        body.put("frecuenciaSistolica", numOrRaw.apply(fila.get("frecuencia sistolica")));
        body.put("frecuenciaDiastolica", numOrRaw.apply(fila.get("frecuencia diastolica")));
        body.put("nivel", numOrRaw.apply(fila.get("nivel")));

        System.out.println(body);
        try {
            ResponseEntity<String> raw = restTemplate.postForEntity(url, body, String.class);

            if (raw.getStatusCode().is2xxSuccessful()) {
                // Éxito -> guardo ingresoJson y limpio responseError
                ingresoJson = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(raw.getBody(), java.util.Map.class);
                responseIngreso = ResponseEntity.status(raw.getStatusCode()).build();
                responseError = null;
            } else {
                // Error 4xx/5xx -> guardo responseError
                responseError = raw;
                responseIngreso = null;
                ingresoJson = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error durante la llamada al endpoint de registro de ingreso", e);
        }
    }

    @Then("el ingreso queda registrado en el sistema")
    public void elIngresoQuedaRegistradoEnElSistema() {
        assertNotNull(ingresoJson, "La request no devolvio el ingreso");
        assertNotNull(ingresoJson.get("id"), "El ingreso devuelto no trae id");
        assertThat(responseIngreso.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @And("el estado inicial del ingreso es {string}")
    public void elEstadoInicialDelIngresoEs(String estadoEsperado) {
        assertThat(ingresoJson.get("estadoIngreso")).isEqualTo(estadoEsperado);
    }

    @And("el paciente entra en la cola de atención")
    public void elPacienteEntraEnLaColaDeAtención() {
        ColaAtencion cola = ingresoService.obtenerCola();

        boolean estaEnCola = cola.contiene(pacienteActual.getCuil().getValor());

        assertTrue(estaEnCola, "El ingreso no está en la cola de atención");
    }

    @Given("que no existe en el sistema el paciente con dni {int}")
    public void queNoExisteEnElSistemaElPacienteConDni(int dni) {

        pacienteRepo.clear();
        pacienteActual = null;
    }

    @Given("que existen los siguientes ingresos en la cola de atención:")
    public void queExistenLosSiguientesIngresosEnLaColaDeAtención(DataTable dataTable) {

        dataTable.asMaps(String.class, String.class).forEach(fila -> {
            Ingreso ingreso = new Ingreso(
                    new Paciente(fila.get("cuil"),fila.get("nombre")),
                    enfermeraActual,
                    NivelEmergencia.fromNumero(Integer.parseInt(fila.get("nivel"))));
            ingreso.setFechaIngreso(LocalDateTime.of(fechaBase, LocalTime.parse(fila.get("hora de ingreso"))));

        });
    }

    @When("ingresa a la cola el paciente con los siguientes datos:")
    public void ingresaALaColaElPacienteConLosSiguientesDatos(DataTable dataTable) {
        Map<String, String> fila = dataTable.asMaps(String.class, String.class).get(0);
        ingresoActual = new Ingreso(
                new Paciente(fila.get("cuil"),fila.get("nombre")),
                enfermeraActual,
                NivelEmergencia.fromNumero(Integer.parseInt(fila.get("nivel")))
        );
        ingresoActual.setFechaIngreso(LocalDateTime.of(fechaBase, LocalTime.parse(fila.get("hora de ingreso"))));
        cola.agregar(ingresoActual);
        posicionResultante = cola.posicionDe(ingresoActual.getPaciente().getCuil().getValor());
    }

    @Then("el nuevo ingreso se ubica en la posición {int} de la cola de atención")
    public void elNuevoIngresoSeUbicaEnLaPosiciónPosicionDeLaColaDeAtención(int posicionEsperada) {

        assertThat(posicionResultante).isEqualTo(posicionEsperada);
    }

    @Then("el sistema muestra un mensaje de error {string}")
    public void elSistemaMuestraUnMensajeDeError(String mensajeEsperado) {
        assertNotNull(responseError, "Se esperaba una respuesta de error pero fue nula");
        assertTrue(responseError.getStatusCode().is4xxClientError(),
                "El estado HTTP debería ser 4xx, pero fue: " + responseError.getStatusCode());
        String cuerpo = responseError.getBody();
        assertNotNull(cuerpo, "El cuerpo del error vino nulo");
        assertTrue(cuerpo.contains(mensajeEsperado),
                () -> "El mensaje esperado era '" + mensajeEsperado + "' pero fue '" + cuerpo + "'");
    }


}
