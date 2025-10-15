package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.service.IngresoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService service;

    public IngresoController(IngresoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody IngresoRequest req) {
        try {
            var ingreso = service.registrarIngreso(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(ingreso);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Para que tus escenarios NEGATIVOS vean el mensaje exacto
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
