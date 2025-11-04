package com.grupo1.ingsw_app.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginStepDefinitions {
    @Given("los siguientes usuarios existen en el sistema")
    public void losSiguientesUsuariosExistenEnElSistema() {
    }

    @And("tienen los siguientes usuarios creados")
    public void tienenLosSiguientesUsuariosCreados() {
    }

    @When("ingreso el usuario {string} y la contraseña {string}")
    public void ingresoElUsuarioYLaContraseña(String arg0, String arg1) {
    }

    @Then("el sistema autentica el usuario con cuil {string}")
    public void elSistemaAutenticaElUsuarioConCuil(String arg0) {
    }

    @Then("el sistema informa que la contraseña es incorrecta")
    public void elSistemaInformaQueLaContraseñaEsIncorrecta() {
    }

    @Then("el sistema informa que el usuario no existe")
    public void elSistemaInformaQueElUsuarioNoExiste() {
    }
}
