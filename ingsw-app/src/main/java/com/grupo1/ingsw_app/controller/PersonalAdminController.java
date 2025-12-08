// src/main/java/com/grupo1/ingsw_app/controller/PersonalAdminController.java
package com.grupo1.ingsw_app.controller;

import com.grupo1.ingsw_app.dtos.PersonalRegistradoDTO;
import com.grupo1.ingsw_app.service.PersonalAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.grupo1.ingsw_app.controller.helpers.RequestParser.*;

@RestController
@RequestMapping("/api/admin/personal")
public class PersonalAdminController {

    private final PersonalAdminService service;

    public PersonalAdminController(PersonalAdminService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<PersonalRegistradoDTO> registrarPersonal(
            @RequestBody Map<String, Object> body) {

        String cuil       = asString(body.get("cuil"), "cuil",
                "es obligatorio indicar el CUIL del personal");
        String nombre     = asString(body.get("nombre"), "nombre",
                "no puede estar vacío");
        String apellido   = asString(body.get("apellido"), "apellido",
                "no puede estar vacío");
        String email      = asString(body.get("email"), "email",
                "no puede estar vacío");

        String matricula  = asString(body.get("matricula"), "matricula",
                "no puede estar vacía");
        String rol        = asString(body.get("rol"), "rol",
                "debe indicarse el rol (DOCTOR / ENFERMERA)");
        String password   = asString(body.get("password"), "password",
                "no puede estar vacía");

        PersonalRegistradoDTO dto = service.registrarPersonal(
                cuil,
                nombre,
                apellido,
                email,
                matricula,
                rol,
                password
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
