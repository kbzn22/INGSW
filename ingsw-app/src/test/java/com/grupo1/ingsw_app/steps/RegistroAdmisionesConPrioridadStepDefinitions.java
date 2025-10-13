package com.grupo1.ingsw_app.steps;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistroAdmisionesConPrioridadStepDefinitions extends CucumberSpringConfiguration{


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IIngresoRepository ingresoRepo;


    @Autowired
    private IPacienteRepository pacienteRepo;

    private ResponseEntity<String> responseError;
    private ResponseEntity<Ingreso> responseIngreso;

    private Paciente pacienteActual;
    Enfermera enfermeraActual;
    Ingreso ingresoActual;

    @Given("la enfermera siguiente enfermera está autenticada en el sistema")
    public void laEnfermeraSiguienteEnfermeraEstáAutenticadaEnElSistema(DataTable table) {
        Map<String, String> row = table.asMaps(String.class, String.class).get(0);

        String nombre    = row.get("Nombre");
        String apellido  = row.get("Apellido");
        Cuil cuil        = new Cuil(row.get("Cuil"));
        String matricula = row.get("Matricula");


        enfermeraActual = new Enfermera(cuil, nombre, apellido, matricula, "");

    }
    @And("existe en el sistema el paciente:")
    public void existeEnElSistemaElPaciente(DataTable dataTable) {
        pacienteRepo.clear();
        Map<String, String> fila = dataTable.asMaps().get(0);
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
        ingresoRepo.clear();

        Map<String, String> r = table.asMaps(String.class, String.class).get(0);
        NivelEmergencia nivelEmergencia = NivelEmergencia.fromNumero(parseInt(r.get("nivel")));

        Ingreso ingreso = new Ingreso(pacienteActual, enfermeraActual, nivelEmergencia);
        ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(parseDouble(r.get("frecuencia cardiaca"))));
        ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(parseDouble(r.get("frecuencia respiratoria"))));
        ingreso.setTensionArterial(new TensionArterial(
                new Frecuencia(parseDouble(r.get("frecuencia sistolica"))),
                new Frecuencia(parseDouble(r.get("frecuencia diastolica")))
        ));
        ingreso.setTemperatura(new Temperatura(parseFloat(r.get("temperatura"))));
        ingreso.setDescripcion(r.get("informe"));

        ingresoRepo.save(ingreso);
        ingresoActual = ingreso;
        responseIngreso = ResponseEntity.status(HttpStatus.CREATED).body(ingreso);
        responseError = null;

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
        assertTrue(ingresoRepo.estaEnCola(ingresoActual), "El ingreso no está en la cola de atención");
    }

    @When("intento registrar el ingreso del paciente con los siguientes datos:")
    public void intentoRegistrarElIngresoDelPacienteConLosSiguientesDatos() {
    }

    @Given("que no existe en el sistema el paciente con dni {int}")
    public void queNoExisteEnElSistemaElPacienteConDni(int arg0) {
    }

    @Given("que existen los siguientes ingresos en la cola de atención:")
    public void queExistenLosSiguientesIngresosEnLaColaDeAtención() {
    }

    @When("registro un nuevo ingreso para el paciente con los siguientes datos:")
    public void registroUnNuevoIngresoParaElPacienteConLosSiguientesDatos() {
    }

    @Then("el nuevo ingreso se ubica en la posición <posicion> de la cola de atención")
    public void elNuevoIngresoSeUbicaEnLaPosiciónPosicionDeLaColaDeAtención() {
    }


}
