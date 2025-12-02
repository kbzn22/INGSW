package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import com.grupo1.ingsw_app.external.IObraSocialClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ObraSocialService {

    private IObraSocialClient obraSocialClient;

    public ObraSocialService(IObraSocialClient obraSocialClient){
        this.obraSocialClient = obraSocialClient;
    }

    public List<ObraSocialDto> listarObrasSociales() {
        return obraSocialClient.listarObrasSociales();
    }

}
