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

        Paciente paciente = service.buscarPorCuil(cuil);
        return ResponseEntity.ok(paciente);

    }
}
