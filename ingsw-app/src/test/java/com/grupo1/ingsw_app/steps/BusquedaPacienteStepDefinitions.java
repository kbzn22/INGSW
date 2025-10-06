package com.grupo1.ingsw_app.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BusquedaPacienteStepDefinitions {

    @Given("la enfermera está autenticada en el sistema")
    public void la_enfermera_está_autenticada_en_el_sistema() {
        throw new io.cucumber.java.PendingException();
    }

    @Given("los pacientes existen en el sistema:")
    public void los_pacientes_existen_en_el_sistema(io.cucumber.datatable.DataTable dataTable) {
        throw new io.cucumber.java.PendingException();
    }

    @When("busco el paciente con dni {string}")
    public void busco_el_paciente_con_dni(String dni) {
        throw new io.cucumber.java.PendingException();
    }

    @Then("el sistema me muestra el paciente:")
    public void el_sistema_me_muestra_el_paciente(io.cucumber.datatable.DataTable dataTable) {
        throw new io.cucumber.java.PendingException();
    }

    @Then("el sistema muestra un mensaje de error {string}")
    public void el_sistema_muestra_un_mensaje_de_error(String mensaje) {
        throw new io.cucumber.java.PendingException();
    }
}
