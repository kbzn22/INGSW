// src/main/java/com/grupo1/ingsw_app/service/ObraSocialService.java
package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ObraSocialService {


    private static final UUID OSDE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PAMI_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final List<ObraSocialDto> obras = List.of(
            new ObraSocialDto(OSDE_ID, "OSDE"),
            new ObraSocialDto(PAMI_ID, "PAMI")
    );


    private final Map<UUID, Set<String>> padronAfiliados = Map.of(
            OSDE_ID, Set.of("OSDE-100", "OSDE-200"),
            PAMI_ID, Set.of("PAMI-300", "PAMI-400")
    );


    public List<ObraSocialDto> listarObrasSociales() {
        return obras;
    }


    public boolean esSocioValido(UUID idObraSocial, String numeroAfiliado, String cuilPaciente) {
        if (idObraSocial == null || numeroAfiliado == null || numeroAfiliado.isBlank()) {
            return false;
        }

        var socios = padronAfiliados.getOrDefault(idObraSocial, Collections.emptySet());


        return socios.contains(numeroAfiliado.trim());
    }
}
