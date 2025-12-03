package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.domain.ColaItem;
import com.grupo1.ingsw_app.dtos.*;
import com.grupo1.ingsw_app.service.AtencionService;
import com.grupo1.ingsw_app.service.IngresoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.grupo1.ingsw_app.controller.helpers.RequestParser.*;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;
    private final AtencionService atencionService;

    public IngresoController(IngresoService ingresoService,
                                   AtencionService atencionService) {
        this.ingresoService = ingresoService;
        this.atencionService = atencionService;
    }

    @PostMapping
    public ResponseEntity<?> registrarIngreso(@RequestBody Map<String, Object> body) {

        String cuilPaciente          = asString(body.get("cuilPaciente"), "cuilPaciente", "es obligatorio indicar el CUIL del paciente");
        String informe               = asString(body.get("informe"), "informe", "no puede estar vacío ni contener solo espacios");
        Float  temperatura           = parseFloat(body.get("temperatura"), "temperatura", "debe tener valores positivos válidos (grados Celsius)");
        Double frecuenciaCardiaca    = parseDouble(body.get("frecuenciaCardiaca"), "frecuenciaCardiaca", "debe tener valores positivos válidos (latidos por minuto)");
        Double frecuenciaRespiratoria= parseDouble(body.get("frecuenciaRespiratoria"), "frecuenciaRespiratoria", "debe tener valores positivos válidos (respiraciones por minuto)");
        Double frecuenciaSistolica   = parseDouble(body.get("frecuenciaSistolica"), "tensionArterial", "debe tener valores positivos válidos para las frecuencias sistólica y diastólica (milímetros de mercurio)");
        Double frecuenciaDiastolica  = parseDouble(body.get("frecuenciaDiastolica"), "tensionArterial", "debe tener valores positivos válidos para las frecuencias sistólica y diastólica (milímetros de mercurio)");
        Integer nivel                = parseInteger(body.get("nivel"), "nivel", "la prioridad ingresada no existe o es nula");

        IngresoRequest req = new IngresoRequest(
                cuilPaciente,
                informe,
                temperatura,
                frecuenciaCardiaca,
                frecuenciaRespiratoria,
                frecuenciaSistolica,
                frecuenciaDiastolica,
                nivel

        );

        var ingreso = ingresoService.registrarIngreso(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(ingreso);
    }

    @GetMapping("/resumen")
    public ResumenColaDTO getResumen() {
        return ingresoService.obtenerResumenCola();
    }

    @GetMapping("/cola")
    public List<PacienteColaDTO> obtenerCola() {
        return ingresoService.obtenerColaDTO(); // ideeal introducir helpers para transformar a dto
    }

    @GetMapping("/en-atencion")
    public ResponseEntity<PacienteEnAtencionDTO> getEnAtencion() {
        return atencionService.obtenerPacienteEnAtencion()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @GetMapping("/{ingresoId}")
    public IngresoDetalleDTO obtenerDetalleIngreso(@PathVariable UUID ingresoId) {
        return ingresoService.obtenerDetalle(ingresoId);
    }
}

