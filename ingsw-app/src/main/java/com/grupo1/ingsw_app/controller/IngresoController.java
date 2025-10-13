package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.service.IngresoService;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
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
    public ResponseEntity<?> registrarIngreso(@RequestBody IngresoRequest request) {
        try {
            Ingreso nuevo = service.registrarIngreso(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        }
    }
}
