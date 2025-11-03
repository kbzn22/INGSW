package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.service.IngresoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService service;

    public IngresoController(IngresoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> registrarIngreso(@RequestBody Map<String, Object> body) {
        Float  temperatura       = parseFloat(body.get("temperatura"));
        Double fc                = parseDouble(body.get("frecuenciaCardiaca"));
        Double fr                = parseDouble(body.get("frecuenciaRespiratoria"));
        Double fsis              = parseDouble(body.get("frecuenciaSistolica"));
        Double fdia              = parseDouble(body.get("frecuenciaDiastolica"));
        Integer nivel            = parseInteger(body.get("nivel"));
        String informe           = asString(body.get("informe"));
        String cuilPaciente      = asString(body.get("cuilPaciente"));
        String cuilEnfermera     = asString(body.get("cuilEnfermera"));

        IngresoRequest req = new IngresoRequest();
        req.setCuilPaciente(cuilPaciente);
        req.setCuilEnfermera(cuilEnfermera);
        req.setInforme(informe);
        req.setTemperatura(temperatura);
        req.setFrecuenciaCardiaca(fc);
        req.setFrecuenciaRespiratoria(fr);
        req.setFrecuenciaSistolica(fsis);
        req.setFrecuenciaDiastolica(fdia);
        req.setNivel(nivel);

        var ingreso = service.registrarIngreso(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingreso);
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static Float parseFloat(Object v) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.floatValue();
            return Float.parseFloat(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException("temperatura",
                    "debe tener valores positivos válidos (grados Celsius)");
        }
    }

    private static Double parseDouble(Object v) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.doubleValue();
            return Double.parseDouble(String.valueOf(v).trim());
        } catch (Exception e) {
            // no sé todavía qué campo, lo manejará el ValueObject
            return null;
        }
    }

    private static Integer parseInteger(Object v) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.intValue();
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException("nivel",
                    "la prioridad ingresada no existe o es nula");
        }
    }
}

