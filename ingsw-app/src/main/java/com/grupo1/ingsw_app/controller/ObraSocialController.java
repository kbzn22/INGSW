// src/main/java/com/grupo1/ingsw_app/controller/ObraSocialController.java
package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import com.grupo1.ingsw_app.service.ObraSocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras-sociales")
public class ObraSocialController {

    private final ObraSocialService obraSocialService;

    public ObraSocialController(ObraSocialService obraSocialService) {
        this.obraSocialService = obraSocialService;
    }

    @GetMapping
    public ResponseEntity<List<ObraSocialDto>> listar() {
        var obras = obraSocialService.listarObrasSociales();
        return ResponseEntity.ok(obras);
    }
}
