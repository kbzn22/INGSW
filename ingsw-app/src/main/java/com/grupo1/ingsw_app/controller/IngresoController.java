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

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService service;

    public IngresoController(IngresoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            // (Opcional) log para ver lo que llega
            // System.out.println(body);

            String informe = asString(body.get("informe"));
            if (informe == null || informe.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("El informe es obligatorio y no puede estar vacío ni contener solo espacios");
            }


            Float  temperatura       = parseFloat(body.get("temperatura"),
                    "La temperatura debe ser un número válido en grados Celsius");
            Double fc                 = parseDouble(body.get("frecuenciaCardiaca"),
                    "La frecuencia cardíaca debe ser un número válido (latidos por minuto)");
            Double fr                 = parseDouble(body.get("frecuenciaRespiratoria"),
                    "La frecuencia respiratoria debe ser un número válido (respiraciones por minuto)");
            Double fsis               = parseDouble(body.get("frecuenciaSistolica"),
                    "La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
            Double fdia               = parseDouble(body.get("frecuenciaDiastolica"),
                    "La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
            Integer nivel             = parseInteger(body.get("nivel"),
                    "La prioridad ingresada no existe o es nula");

            String cuilPaciente       = asString(body.get("cuilPaciente"));
            String cuilEnfermera      = asString(body.get("cuilEnfermera"));


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

        } catch (IllegalArgumentException | IllegalStateException ex) {

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }
    private static Float parseFloat(Object v, String msg) {
        if (v == null) throw new IllegalArgumentException(msg);
        try {
            if (v instanceof Number n) return n.floatValue();
            return Float.parseFloat(String.valueOf(v).trim());
        } catch (Exception e) { throw new IllegalArgumentException(msg); }
    }
    private static Double parseDouble(Object v, String msg) {
        if (v == null) throw new IllegalArgumentException(msg);
        try {
            if (v instanceof Number n) return n.doubleValue();
            return Double.parseDouble(String.valueOf(v).trim());
        } catch (Exception e) { throw new IllegalArgumentException(msg); }
    }
    private static Integer parseInteger(Object v, String msg) {
        if (v == null) throw new IllegalArgumentException(msg);
        try {
            if (v instanceof Number n) return n.intValue();
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Exception e) { throw new IllegalArgumentException(msg); }
    }
}
