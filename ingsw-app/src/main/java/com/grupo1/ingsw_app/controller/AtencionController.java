// src/main/java/com/grupo1/ingsw_app/controller/AtencionController.java
package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.AtencionLogDTO;

import com.grupo1.ingsw_app.dtos.PacienteEnAtencionDTO;

import com.grupo1.ingsw_app.service.AtencionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/medico")
public class AtencionController {

    private final AtencionService service;


    public AtencionController(AtencionService service) {
        this.service = service;
    }

    @PostMapping("/{ingresoId}/iniciar")
    public ResponseEntity<PacienteEnAtencionDTO> iniciar(@PathVariable UUID ingresoId) {
        var dto = service.iniciarAtencion(ingresoId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{ingresoId}/finalizar")
    public ResponseEntity<PacienteEnAtencionDTO> finalizar(
            @PathVariable UUID ingresoId,
            @RequestBody Map<String, String> body) {

        String informe = body.getOrDefault("informe", "");
        var dto = service.finalizarAtencion(ingresoId, informe);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/atenciones/{id}")
    public ResponseEntity<AtencionLogDTO> getById(@PathVariable UUID id) {
        AtencionLogDTO dto = service.obtenerDetalleAtencion(id);
        return ResponseEntity.ok(dto);
    }
}
