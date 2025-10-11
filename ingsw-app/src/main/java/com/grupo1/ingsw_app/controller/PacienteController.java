package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.service.PacienteService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping("/{cuil}")
    public ResponseEntity<?> buscarPorCuil(@PathVariable String cuil) {

        try {
            Paciente paciente = service.buscarPorCuil(cuil);
            return ResponseEntity.ok(paciente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        }

    }
}
