package com.grupo1.ingsw_app;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "com.grupo1.ingsw_app.steps",
        plugin = {"pretty", "summary"}
)
public class RunCucumberTest {
}
