package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.service.IngresoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.grupo1.ingsw_app.controller.helpers.RequestParser.*;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService service;

    public IngresoController(IngresoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> registrarIngreso(@RequestBody Map<String, Object> body) {

        String cuilPaciente         = asString(body.get("cuilPaciente"), "cuilPaciente", "es obligatorio indicar el CUIL del paciente");
        //String cuilEnfermera        = asString(body.get("cuilEnfermera"), "cuilEnfermera", "es obligatorio indicar el CUIL de la enfermera");
        String informe              = asString(body.get("informe"), "informe", "no puede estar vacío ni contener solo espacios");
        Float  temperatura           = parseFloat(body.get("temperatura"), "temperatura", "debe tener valores positivos válidos (grados Celsius)");
        Double frecuenciaCardiaca    = parseDouble(body.get("frecuenciaCardiaca"), "frecuenciaCardiaca", "debe tener valores positivos válidos (latidos por minuto)");
        Double frecuenciaRespiratoria= parseDouble(body.get("frecuenciaRespiratoria"), "frecuenciaRespiratoria", "debe tener valores positivos válidos (respiraciones por minuto)");
        Double frecuenciaSistolica   = parseDouble(body.get("frecuenciaSistolica"), "tensionArterial", "debe tener valores positivos válidos para las frecuencias sistólica y diastólica (milimetros de mercurio)");
        Double frecuenciaDiastolica  = parseDouble(body.get("frecuenciaDiastolica"), "tensionArterial", "debe tener valores positivos válidos para las frecuencias sistólica y diastólica (milimetros de mercurio)");
        Integer nivel                = parseInteger(body.get("nivel"), "nivel", "la prioridad ingresada no existe o es nula");

        IngresoRequest req = new IngresoRequest(
                cuilPaciente,
                //cuilEnfermera,
                informe,
                temperatura,
                frecuenciaCardiaca,
                frecuenciaRespiratoria,
                frecuenciaSistolica,
                frecuenciaDiastolica,
                nivel
        );

        var ingreso = service.registrarIngreso(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingreso);
    }
}

