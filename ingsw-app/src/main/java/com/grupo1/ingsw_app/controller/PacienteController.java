package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.dtos.PacienteRequest;
import com.grupo1.ingsw_app.service.PacienteService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.UUID;

import static com.grupo1.ingsw_app.controller.helpers.RequestParser.*;

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

    @PostMapping
        public ResponseEntity<?> registrarPaciente(@RequestBody Map<String, Object> body) {

            String cuil         = asString(body.get("cuilPaciente"), "cuilPaciente", "es obligatorio indicar el CUIL del paciente");
            String nombre       = asString(body.get("nombre"), "nombre", "no puede estar vacío");
            String apellido     = asString(body.get("apellido"), "apellido", "no puede estar vacío");
            String email        = asString(body.get("email"), "email", "no puede estar vacío");
            String calle        = asString(body.get("calle"), "calle", "no puede estar vacía");
            Integer numero       = parseInteger(body.get("numero"), "numero", "no puede estar vacía");
            String localidad    = asString(body.get("localidad"), "localidad", "no puede estar vacía");

            UUID idObraSocial = null;
            String numeroAfiliado = null;

            Object idObraSocialRaw = body.get("idObraSocial");
            if (idObraSocialRaw != null) {
                idObraSocial = parseUUID(idObraSocialRaw, "idObraSocial", "debe ser un UUID válido");
                numeroAfiliado = asString(body.get("numeroAfiliado"), "numeroAfiliado", "es obligatorio si hay obra social");
            }

            PacienteRequest req = new PacienteRequest(
                    cuil,
                    nombre,
                    apellido, email,
                    calle,
                    numero,
                    localidad,
                    idObraSocial,
                    numeroAfiliado
            );

            var paciente = service.registrarPaciente(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(paciente);

        }
}
