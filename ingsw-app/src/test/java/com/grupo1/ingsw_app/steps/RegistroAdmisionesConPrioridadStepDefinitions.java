package com.grupo1.ingsw_app.steps;

import com.grupo1.ingsw_app.domain.*;
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
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistroAdmisionesConPrioridadStepDefinitions {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IPacienteRepository pacienteRepo;

    @Autowired
    private IIngresoRepository ingresoRepo;

    private ResponseEntity<String> responseError;
    private ResponseEntity<Ingreso> responseIngreso;

    private Paciente pacienteActual;

    @And("existe en el sistema el paciente:")
    public void existeEnElSistemaElPaciente(DataTable dataTable) {
        pacienteRepo.clear();
        Map<String, String> fila = dataTable.asMaps().get(0);
        Paciente p = new Paciente(fila.get("dni"), fila.get("nombre"));
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
        Map<String, String> r = table.asMaps().get(0);

        try {
            // Validaciones mínimas (similar a la capa REST)
            String informe = r.get("informe");
            if (informe == null || informe.trim().isEmpty())
                throw new IllegalArgumentException("El informe es obligatorio y no puede estar vacío ni contener solo espacios");

            double temp;
            try {
                temp = parseDouble(r.get("temperatura"));
            } catch (Exception e) {
                throw new IllegalArgumentException("La temperatura debe ser un número válido en grados Celsius");
            }

            double fc;
            try {
                fc = parseDouble(r.get("frecuencia cardiaca"));
            } catch (Exception e) {
                throw new IllegalArgumentException("La frecuencia cardíaca debe ser un número válido (latidos por minuto)");
            }

            double fr;
            try {
                fr = parseDouble(r.get("frecuencia respiratoria"));
            } catch (Exception e) {
                throw new IllegalArgumentException("La frecuencia respiratoria debe ser un número válido (respiraciones por minuto)");
            }

            double fsis;
            try {
                fsis = parseDouble(r.get("frecuencia sistolica"));
            } catch (Exception e) {
                throw new IllegalArgumentException("La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
            }

            double fdia;
            try {
                fdia = parseDouble(r.get("frecuencia diastolica"));
            } catch (Exception e) {
                throw new IllegalArgumentException("La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
            }

            int nivelN;
            try {
                nivelN = parseInt(r.get("nivel"));
            } catch (Exception e) {
                throw new IllegalArgumentException("La prioridad ingresada no existe o es nula");
            }


            if (temp < 0 || fc < 0 || fr < 0 || fsis < 0 || fdia < 0)
                throw new IllegalArgumentException("Los signos vitales no pueden ser negativos");

            NivelEmergencia ne = NivelEmergencia.fromNumero(nivelN);

            // Construcción del ingreso (sin HTTP, directo al repo)
            Ingreso ingreso = new Ingreso(pacienteActual, new Enfermera("Susana","Gimenez","20-12345604-4", "ahahaha@gmail.com","ENF-001"), ne);
            ingreso.setDescripcion(informe);
            ingreso.setTemperatura(new Temperatura(temp));
            ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(fc));
            ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(fr));
            ingreso.setTensionArterial(new TensionArterial(new Frecuencia(fsis), new Frecuencia(fdia)));

            ingresoRepo.save(ingreso);
            responseIngreso = ResponseEntity.status(HttpStatus.CREATED).body(ingreso);
            responseError = null;

        } catch (IllegalArgumentException ex) {
            responseError = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
            responseIngreso = null;
        }

    }

    @Then("el ingreso queda registrado en el sistema")
    public void elIngresoQuedaRegistradoEnElSistema() {
    }

    @And("el estado inicial del ingreso es {string}")
    public void elEstadoInicialDelIngresoEs(String arg0) {
    }

    @And("el sistema registra a la enfermera responsable en el ingreso")
    public void elSistemaRegistraALaEnfermeraResponsableEnElIngreso() {
    }

    @And("el paciente entra en la cola de atención")
    public void elPacienteEntraEnLaColaDeAtención() {
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
