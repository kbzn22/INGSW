package com.grupo1.ingsw_app.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class urgenciasStepDefinitions {
    @Given("que existe el paciente {string} en el sistema")
    public void que_existe_el_paciente_en_el_sistema(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("se registra su ingreso junto con los datos:")
    public void se_registra_su_ingreso_junto_con_los_datos(io.cucumber.datatable.DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        throw new io.cucumber.java.PendingException();
    }
    @Then("el ingreso queda registrado en el sistema")
    public void el_ingreso_queda_registrado_en_el_sistema() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("el paciente entra en la cola de atención")
    public void el_paciente_entra_en_la_cola_de_atención() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("el estado inicial del ingreso es {string}")
    public void el_estado_inicial_del_ingreso_es(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
