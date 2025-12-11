package com.grupo1.ingsw_app.external;

import com.grupo1.ingsw_app.domain.ObraSocial;
import com.grupo1.ingsw_app.dtos.ObraSocialDto;

import java.util.List;
import java.util.UUID;

public interface IObraSocialClient {

    ObraSocial buscarPorId(UUID id);

    boolean estaAfiliado(UUID idObraSocial, String numeroAfiliado);

    List<ObraSocialDto> listarObrasSociales();
}