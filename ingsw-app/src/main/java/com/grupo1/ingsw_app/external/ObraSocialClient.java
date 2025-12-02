package com.grupo1.ingsw_app.external;

import com.grupo1.ingsw_app.domain.ObraSocial;
import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ObraSocialClient implements IObraSocialClient {

    // ==== Datos mockeados ====
    private static final UUID OSDE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PAMI_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID SWISS_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private final List<ObraSocialDto> obras = List.of(
            new ObraSocialDto(OSDE_ID, "OSDE"),
            new ObraSocialDto(PAMI_ID, "PAMI"),
            new ObraSocialDto(SWISS_ID, "SWISS MEDICAL")
    );

    // padrón de afiliados simulados
    private final Map<UUID, Set<String>> padronAfiliados = Map.of(
            OSDE_ID, Set.of("OSDE-100", "OSDE-200"),
            PAMI_ID, Set.of("PAMI-300", "PAMI-400"),
            SWISS_ID, Set.of("SWISS-500", "SWISS-600")
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
