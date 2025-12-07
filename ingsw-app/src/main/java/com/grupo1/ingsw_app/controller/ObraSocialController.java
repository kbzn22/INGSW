package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.ObraSocialDto;
import com.grupo1.ingsw_app.service.IngresoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras-sociales")
public class ObraSocialController {

    private final IngresoService ingresoService;

    public ObraSocialController(IngresoService ingresoService) {
        this.ingresoService = ingresoService;
    }

    @GetMapping
    public ResponseEntity<List<ObraSocialDto>> listar() {
        var obras = ingresoService.listarObrasSociales();
        return ResponseEntity.ok(obras);
    }
}
