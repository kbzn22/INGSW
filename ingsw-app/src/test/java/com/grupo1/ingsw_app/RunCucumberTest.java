package com.grupo1.ingsw_app;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;

/**
 * Runner principal de Cucumber + JUnit 5.
 * Ejecuta todos los .feature ubicados en src/test/resources/features
 * y los enlaza con las Step Definitions en com.grupo1.ingsw_app.steps
 */
@Suite
@Cucumber
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.grupo1.ingsw_app.steps")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "classpath:features")
public class RunCucumberTest {
    // No necesita código: JUnit usa las anotaciones de arriba
}
