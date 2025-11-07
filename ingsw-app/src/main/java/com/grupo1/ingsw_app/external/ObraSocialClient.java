package com.grupo1.ingsw_app.external;

import com.grupo1.ingsw_app.domain.ObraSocial;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ObraSocialClient implements IObraSocialClient {

    @Override
    public ObraSocial buscarPorId(UUID id) {
        return null;
    }

    @Override
    public boolean estaAfiliado(UUID idObraSocial, String numeroAfiliado) {
        return false;
    }
}