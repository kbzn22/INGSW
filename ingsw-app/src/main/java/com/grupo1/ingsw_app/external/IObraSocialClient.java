package com.grupo1.ingsw_app.external;

import com.grupo1.ingsw_app.domain.ObraSocial;

import java.util.UUID;

public interface IObraSocialClient {

    /**
     * Busca una obra social por su identificador.
     * @param id Identificador UUID de la obra social
     * @return ObraSocial encontrada o null si no existe
     */
    ObraSocial buscarPorId(UUID id);

    /**
     * Verifica si un número de afiliado pertenece a una obra social.
     * @param idObraSocial Identificador de la obra social
     * @param numeroAfiliado Número de afiliado provisto por el paciente
     * @return true si el número está afiliado, false en caso contrario
     */
    boolean estaAfiliado(UUID idObraSocial, String numeroAfiliado);
}