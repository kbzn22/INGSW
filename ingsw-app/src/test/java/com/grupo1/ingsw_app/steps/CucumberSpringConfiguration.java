package com.grupo1.ingsw_app.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import com.grupo1.ingsw_app.IngswAppApplication;

@CucumberContextConfiguration
@SpringBootTest(
        classes = IngswAppApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CucumberSpringConfiguration {
}
