package com.grupo1.ingsw_app.external;

import com.grupo1.ingsw_app.domain.ObraSocial;
import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ObraSocialClient implements IObraSocialClient {


    private static final UUID OSDE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PAMI_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID SWISS_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID SS_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private final List<ObraSocialDto> obras = List.of(
            new ObraSocialDto(OSDE_ID, "OSDE"),
            new ObraSocialDto(PAMI_ID, "PAMI"),
            new ObraSocialDto(SWISS_ID, "SWISS MEDICAL"),
            new ObraSocialDto(SS_ID, "SUBSIDIO DE SALUD")
    );


    private final Map<UUID, Set<String>> padronAfiliados = Map.of(
            OSDE_ID, Set.of("OSDE-100", "OSDE-200","OSDE-300"),
            PAMI_ID, Set.of("PAMI-400", "PAMI-500", "PAMI-600"),
            SWISS_ID, Set.of("SWISS-700", "SWISS-800", "SWISS-900"),
            SS_ID, Set.of("SS-100", "SS-101","SS-102")
    );

    @Override
    public ObraSocial buscarPorId(UUID id) {
        // Busca en la lista mockeada
        return obras.stream()
                .filter(o -> o.id().equals(id))
                .findFirst()
                .map(o -> new ObraSocial(o.id(), o.nombre()))
                .orElse(null);
    }

    @Override
    public boolean estaAfiliado(UUID idObraSocial, String numeroAfiliado) {
        if (idObraSocial == null || numeroAfiliado == null || numeroAfiliado.isBlank()) {
            return false;
        }

        var socios = padronAfiliados.getOrDefault(idObraSocial, Collections.emptySet());
        return socios.contains(numeroAfiliado.trim());
    }

    @Override
    public List<ObraSocialDto> listarObrasSociales() {
        return obras;
    }
}
