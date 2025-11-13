package com.grupo1.ingsw_app.steps;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class Hooks {

    @Autowired
    JdbcTemplate jdbc;

    @Before
    public void limpiarBaseAntesDeCadaEscenario() {
        System.out.println(">>> TRUNCATE ejecutado");
        jdbc.execute("TRUNCATE TABLE ingreso, paciente, obra_social, sesion, usuario_personal, personal RESTART IDENTITY CASCADE");
    }
}
