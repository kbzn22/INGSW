package com.grupo1.ingsw_app.domain.valueobjects;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CuilTest {

    @Test
    void SiCreoUnCuilValido_DeberiaCrearseCorrectamente() {
        //Arrange
        String cuilEsperado = "20-12345678-9";

        //Act
        String cuilResultado = new Cuil(cuilEsperado).getValor();

        //Assert
        assertEquals(cuilEsperado, cuilResultado);
    }

    @Test
    void SiCreoUnCuilConNumerosSinGuiones_DeberiaTirarUnaExcepcion() {
        //Arrange
        String cuilEsperado = "20123456789";

        //Act & Assert
        assertThrows(
                CampoInvalidoException.class,
                () -> new Cuil(cuilEsperado).getValor(),
                "Debe lanzar CampoInvalidoException si el CUIL no contiene guiones"
        );
    }

    @Test
    void SiCreoUnCuilAlfanumerico_DeberiaTirarUnaExcepcion() {
        //Arrange
        String cuilEsperado = "20-AB345678-9";

        //Act & Assert
        assertThrows(
                CampoInvalidoException.class,
                () -> new Cuil(cuilEsperado).getValor(),
                "Debe lanzar CampoInvalidoException si el CUIL es alfanumerico"
        );
    }
}
