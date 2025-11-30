package com.grupo1.ingsw_app.steps;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;



public class Hooks extends CucumberSpringConfiguration{

    @Autowired
    JdbcTemplate jdbc;

    @Before
    @Transactional
    public void limpiarBaseAntesDeCadaEscenario() {
        String[] tablas = {
                "ingreso",
                "paciente",
                "obra_social",
                "sesion",
                "usuario_personal",
                "personal"
        };

        for (String t : tablas) {
            try {
                jdbc.execute("TRUNCATE TABLE " + t + " RESTART IDENTITY CASCADE");
                System.out.println("TRUNCATE OK -> " + t);
            } catch (Exception e) {
                System.out.println("NO SE PUDO TRUNCAR -> " + t + " (" + e.getMessage() + ")");
            }
        }
    }
}


