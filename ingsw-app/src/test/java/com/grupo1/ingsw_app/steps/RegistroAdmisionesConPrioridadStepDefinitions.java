package com.grupo1.ingsw_app.steps;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.NivelEmergencia;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;


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

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
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

    private ResponseEntity<String> responseError;
    private ResponseEntity<Ingreso> responseIngreso;

    private Paciente pacienteActual;
    private LocalDate fechaBase = LocalDate.of(2025, 10, 12);
    private int posicionResultante;
    Enfermera enfermeraActual;
    Ingreso ingresoActual;

    @Given("la siguiente enfermera está autenticada en el sistema")
    public void laEnfermeraSiguienteEnfermeraEstáAutenticadaEnElSistema(DataTable table) {
        Map<String, String> fila = table.asMaps(String.class, String.class).get(0);
        enfermeraActual = new Enfermera(fila.get("Cuil"), fila.get("Nombre"), fila.get("Apellido"), fila.get("Matricula"), "");
    }

    @And("existe en el sistema el paciente:")
    public void existeEnElSistemaElPaciente(DataTable dataTable) {
        pacienteRepo.clear();
        Map<String, String> fila = dataTable.asMaps(String.class, String.class).get(0);
        Paciente p = new Paciente(fila.get("cuil"), fila.get("nombre"));
        pacienteRepo.save(p);
        pacienteActual = p;
    }

    @And("existen las prioridades de emergencia:")
    public void existenLasPrioridadesDeEmergencia(DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            int nivelNum = parseInt(row.get("nivel"));
            NivelEmergencia ne = NivelEmergencia.fromNumero(nivelNum);
            assertThat(ne.getNivel().getNombre()).isNotEmpty();
        });
    }

    @When("registro el ingreso del paciente con los siguientes datos:")
    public void registroElIngresoDelPacienteConLosSiguientesDatos(DataTable table) {
        Map<String, String> r = table.asMaps(String.class, String.class).get(0);

        String url = "http://localhost:" + port + "/api/ingresos";

        // Construimos el cuerpo de la request como JSON
        Map<String, Object> request = Map.of(
                "cuilPaciente", pacienteActual.getCuil().getValor(),
                "cuilEnfermera", enfermeraActual.getCuil().getValor(),
                "informe", r.get("informe"),
                "temperatura", parseFloat(r.get("temperatura")),
                "frecuenciaCardiaca", parseDouble(r.get("frecuencia cardiaca")),
                "frecuenciaRespiratoria", parseDouble(r.get("frecuencia respiratoria")),
                "frecuenciaSistolica", parseDouble(r.get("frecuencia sistolica")),
                "frecuenciaDiastolica", parseDouble(r.get("frecuencia diastolica")),
                "nivel", parseInt(r.get("nivel"))
        );

        try {
            // Enviar POST y obtener SIEMPRE respuesta como String
            ResponseEntity<String> rawResponse =
                    restTemplate.postForEntity(url, request, String.class);

            if (rawResponse.getStatusCode().is2xxSuccessful()) {
                // Si es 201 o 200, parseamos manualmente el JSON
                ObjectMapper mapper = new ObjectMapper();
                Ingreso ingreso = mapper.readValue(rawResponse.getBody(), Ingreso.class);

                ingresoActual = ingreso;
                responseIngreso = ResponseEntity.status(rawResponse.getStatusCode()).body(ingreso);
                responseError = null;
            } else {
                // Si es error (400, 404, etc.)
                responseError = rawResponse;
                responseIngreso = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error durante la llamada al endpoint de registro de ingreso", e);
        }
    }

    @Then("el ingreso queda registrado en el sistema")
    public void elIngresoQuedaRegistradoEnElSistema() {
        assertNotNull(ingresoActual, "El ingreso no fue creado");
        assertTrue(ingresoRepo.existsById(ingresoActual.getId()), "El ingreso no se persistió en el repositorio");
        assertThat(responseIngreso.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @And("el estado inicial del ingreso es {string}")
    public void elEstadoInicialDelIngresoEs(String estadoEsperado) {
        assertNotNull(ingresoActual, "El ingreso no fue creado");
        assertThat(ingresoActual.getEstadoIngreso().name()).isEqualTo(estadoEsperado);
    }

    @And("el paciente entra en la cola de atención")
    public void elPacienteEntraEnLaColaDeAtención() {
        assertNotNull(ingresoActual, "El ingreso no fue creado");
        assertTrue(ingresoService.estaEnCola(ingresoActual), "El ingreso no está en la cola de atención");
    }

    @When("intento registrar el ingreso del paciente con los siguientes datos:")
    public void intentoRegistrarElIngresoDelPacienteConLosSiguientesDatos() {
    }

    @Given("que no existe en el sistema el paciente con dni {int}")
    public void queNoExisteEnElSistemaElPacienteConDni(int arg0) {
    }

    @Given("que existen los siguientes ingresos en la cola de atención:")
    public void queExistenLosSiguientesIngresosEnLaColaDeAtención(DataTable dataTable) {
        ingresoService.limpiarIngresos();

        dataTable.asMaps(String.class, String.class).forEach(fila -> {
            Paciente paciente = new Paciente(fila.get("cuil"), fila.get("nombre"));
            NivelEmergencia nivel = NivelEmergencia.fromNumero(Integer.parseInt(fila.get("nivel")));
            LocalDateTime hora = LocalDateTime.of(fechaBase, LocalTime.parse(fila.get("hora de ingreso")));

            Ingreso ingreso = new Ingreso(paciente, nivel, hora);
            ingresoService.registrarIngreso(ingreso);
        });
    }

    @When("registro un nuevo ingreso para el paciente con los siguientes datos:")
    public void registroUnNuevoIngresoParaElPacienteConLosSiguientesDatos(DataTable dataTable) {
        Map<String, String> fila = dataTable.asMaps().get(0);

        Paciente paciente = new Paciente(fila.get("cuil"), fila.get("nombre"));
        NivelEmergencia nivel = NivelEmergencia.fromNumero(Integer.parseInt(fila.get("nivel")));
        LocalDateTime fechaHora = LocalDateTime.of(fechaBase, LocalTime.parse(fila.get("hora de ingreso")));

        Ingreso nuevoIngreso = new Ingreso(paciente, nivel, fechaHora);
        ingresoService.registrarIngreso(nuevoIngreso);

        posicionResultante = ingresoService.posicionEnLaCola(paciente.getCuil().getValor());
    }

    @Then("el nuevo ingreso se ubica en la posición {int} de la cola de atención")
    public void elNuevoIngresoSeUbicaEnLaPosiciónPosicionDeLaColaDeAtención(int posicionEsperada) {
        assertThat(posicionResultante).isEqualTo(posicionEsperada);
    }
}
