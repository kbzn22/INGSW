package com.grupo1.ingsw_app.steps;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.NivelEmergencia;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;


import com.grupo1.ingsw_app.persistance.IPersonalRepository;
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

    @Autowired
    private IPersonalRepository personalRepo;

    private ResponseEntity<String> responseError;
    private ResponseEntity<Ingreso> responseIngreso;

    private Paciente pacienteActual;
    private LocalDate fechaBase = LocalDate.of(2025, 10, 12);
    private int posicionResultante;
    Enfermera enfermeraActual;
    Ingreso ingresoActual;
    private Map<String, Object> ingresoJson;

    @Given("la siguiente enfermera está autenticada en el sistema")
    public void laEnfermeraSiguienteEnfermeraEstáAutenticadaEnElSistema(DataTable table) {
        Map<String, String> fila = table.asMaps(String.class, String.class).get(0);

        Enfermera enfermera = new Enfermera(fila.get("Cuil"), fila.get("Nombre"), fila.get("Apellido"), fila.get("Matricula"), "");

        personalRepo.save(enfermera);

        enfermeraActual=enfermera;
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

        Map<String, String> fila = table.asMaps(String.class, String.class).get(0);

        String url = "http://localhost:" + port + "/api/ingresos";

        // Construimos el cuerpo de la request como JSON
        IngresoRequest request = new IngresoRequest();
        request.setCuilPaciente(pacienteActual.getCuil().getValor());
        request.setCuilEnfermera(enfermeraActual.getCuil().getValor());
        request.setInforme(fila.get("informe"));
        request.setTemperatura(Float.parseFloat(fila.get("temperatura")));
        request.setFrecuenciaCardiaca(Double.parseDouble(fila.get("frecuencia cardiaca")));
        request.setFrecuenciaRespiratoria(Double.parseDouble(fila.get("frecuencia respiratoria")));
        request.setFrecuenciaSistolica(Double.parseDouble(fila.get("frecuencia sistolica")));
        request.setFrecuenciaDiastolica(Double.parseDouble(fila.get("frecuencia diastolica")));
        request.setNivel(Integer.parseInt(fila.get("nivel")));


        try {
            ResponseEntity<String> rawResponse =
                    restTemplate.postForEntity(url, request, String.class);

            if (rawResponse.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();

                ingresoJson = mapper.readValue(rawResponse.getBody(), Map.class);
                ingresoActual = null; // ya no lo usamos acá
                responseIngreso = ResponseEntity.status(rawResponse.getStatusCode()).build();
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
        assertNotNull(ingresoJson, "No se obtuvo el ingreso de la respuesta");
        assertNotNull(ingresoJson.get("id"), "La respuesta no trae id");
        assertThat(responseIngreso.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @And("el estado inicial del ingreso es {string}")
    public void elEstadoInicialDelIngresoEs(String estadoEsperado) {
        assertNotNull(ingresoJson);
        assertThat(ingresoJson.get("estadoIngreso")).isEqualTo(estadoEsperado);
    }

    @And("el paciente entra en la cola de atención")
    public void elPacienteEntraEnLaColaDeAtención() {
        assertNotNull(pacienteActual, "No hay paciente actual");
        int pos = ingresoService.posicionEnLaCola(pacienteActual.getCuil().getValor());
        assertTrue(pos > 0, "El ingreso no está en la cola de atención");
    }

    @Given("que no existe en el sistema el paciente con dni {int}")
    public void queNoExisteEnElSistemaElPacienteConDni(int dni) {
        // Tu dominio usa CUIL; para este Given garantizamos que no exista el paciente actual
        pacienteRepo.clear();
        pacienteActual = null;
    }

    @Given("que existen los siguientes ingresos en la cola de atención:")
    public void queExistenLosSiguientesIngresosEnLaColaDeAtención(DataTable dataTable) {
        ingresoService.limpiarIngresos();


        dataTable.asMaps(String.class, String.class).forEach(row -> {
            // asegurar paciente
            Paciente p = pacienteRepo.findByCuil(row.get("cuil"))
                    .orElseGet(() -> {
                        Paciente nuevo = new Paciente(row.get("cuil"), row.get("nombre"));
                        pacienteRepo.save(nuevo);
                        return nuevo;
                    });

            // request mínimo válido
            IngresoRequest req = new IngresoRequest();
            req.setCuilPaciente(p.getCuil().getValor());
            req.setCuilEnfermera(enfermeraActual.getCuil().getValor());
            req.setInforme("ingreso de prueba");
            req.setTemperatura(36.5f);
            req.setFrecuenciaCardiaca(80);
            req.setFrecuenciaRespiratoria(16);
            req.setFrecuenciaSistolica(120);
            req.setFrecuenciaDiastolica(80);
            req.setNivel(Integer.parseInt(row.get("nivel")));

            // registrar (esto guarda y encola)
            Ingreso i = ingresoService.registrarIngreso(req);


        });
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
