package com.grupo1.ingsw_app.controller.helpers;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RequestParserTest {
    // ---------- asString ----------
    @Test
    void asString_siEntradaEsValida_devuelveCadenaValida() {
        //Arrange
        String esperado = "Enzo";

        //Act
        String resultado = RequestParser.asString(" Enzo   ", "nombre", "no puede estar vacío");

        //Assert
        assertEquals(esperado, resultado);
    }

    @Test
    void asString_siEsNullOVacio_lanzaExcepcion() {
        //Arrange
        String nulo = null;
        String vacio = "   ";

        //Act & Assert
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.asString(nulo, "nombre", "no puede estar vacío"));
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.asString(vacio, "nombre", "no puede estar vacío"));
    }

    // ---------- parseInteger ----------
    @Test
    void parseInteger_siEntradaEsValida_convierteCorrectamente() {
        //Arrange
        Integer esperado1 = 42;
        Integer esperado2 = -15;

        //Act
        Integer resultado1 = RequestParser.parseInteger("42", "numero", "debe ser entero");
        Integer resultado2 = RequestParser.parseInteger(-15, "numero", "debe ser entero");

        //Assert
        assertEquals(esperado1, resultado1);
        assertEquals(esperado2, resultado2);
    }

    @Test
    void parseInteger_siEsNull_devuelveNull() {
        //Act
        Integer resultado = RequestParser.parseInteger(null, "numero", "mensaje");

        //Assert
        assertNull(resultado);
    }

    @Test
    void parseInteger_siEsNumeroNoEntero_LanzaExcepcion() {
        //Arrange
        Double entrada = 26.8;

        //Act & Assert
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.parseInteger(entrada, "numero", "debe ser entero"));
    }

    @Test
    void parseInteger_siNoEsNumero_LanzaExcepcion() {
        //Arrange
        String entrada = "abcw2";

        //Act & Assert
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.parseInteger(entrada, "numero", "debe ser entero"));
    }

    // ---------- parseUUID ----------
    @Test
    void parseUUID_siEntradaEsValida_convierteCorrectamente() {
        //Arrange
        UUID esperado = UUID.randomUUID();
        String entrada = esperado.toString();

        //Act
        UUID resultado = RequestParser.parseUUID(entrada, "id", "UUID inválido");

        //Assert
        assertEquals(esperado, resultado);
    }

    @Test
    void parseUUID_siEsNull_devuelveNull() {
        //Act
        UUID resultado = RequestParser.parseUUID(null, "id", "UUID inválido");

        //Assert
        assertNull(resultado);
    }

    @Test
    void parseUUID_siFormatoEsInvalido_lanzaExcepcion() {
        //Arrange
        String entrada = "no-es-uuid";

        //Act & Assert
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.parseUUID(entrada, "id", "UUID inválido"));
    }

    // ---------- parseFloat ----------
    @Test
    void parseFloat_siEntradaEsValida_devuelveValorValido() {
        //Arrange
        Float esperado1 = 3.14f;
        Float esperado2 = -10.5f;

        //Act
        Float resultado1 = RequestParser.parseFloat("3.14", "temperatura", "inválido");
        Float resultado2 = RequestParser.parseFloat(-10.5f, "temperatura", "inválido");

        //Assert
        assertEquals(esperado1, resultado1);
        assertEquals(esperado2, resultado2);
    }

    @Test
    void parseFloat_siEsNull_devuelveNull() {
        //Act
        Float resultado = RequestParser.parseFloat(null, "temperatura", "inválido");

        //Assert
        assertNull(resultado);
    }

    @Test
    void parseFloat_siNoEsNumero_lanzaExcepcion() {
        //Arrange
        String entrada = "abc";

        //Act & Assert
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.parseFloat(entrada, "temperatura", "inválido"));
    }


    // ---------- parseDouble ----------
    @Test
    void parseDouble_siEntradaEsValida_devuelveValorValido() {
        //Arrange
        Double esperado1 = 12.5;
        Double esperado2 = -7.8;

        //Act
        Double resultado1 = RequestParser.parseDouble("12.5", "valor", "inválido");
        Double resultado2 = RequestParser.parseDouble(-7.8, "valor", "inválido");

        //Assert
        assertEquals(esperado1, resultado1);
        assertEquals(esperado2, resultado2);
    }

    @Test
    void parseDouble_siEsNull_devuelveNull() {
        //Act
        Double resultado = RequestParser.parseDouble(null, "valor", "inválido");

        //Assert
        assertNull(resultado);
    }

    @Test
    void parseDouble_siNoEsNumero_lanzaExcepcion() {
        //Arrange
        String entrada = "abc";

        //Act & Assert
        assertThrows(CampoInvalidoException.class,
                () -> RequestParser.parseDouble(entrada, "valor", "inválido"));
    }
}
