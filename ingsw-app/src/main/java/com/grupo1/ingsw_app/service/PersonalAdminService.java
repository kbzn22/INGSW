// src/main/java/com/grupo1/ingsw_app/service/PersonalAdminService.java
package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.dtos.PersonalRegistradoDTO;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.persistence.IPersonalRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonalAdminService {

    private final IPersonalRepository personalRepo;
    private final PasswordEncoder encoder;

    public PersonalAdminService(IPersonalRepository personalRepo,
                                PasswordEncoder encoder) {
        this.personalRepo = personalRepo;
        this.encoder = encoder;
    }

    public PersonalRegistradoDTO registrarPersonal(
            String cuil,
            String nombre,
            String apellido,
            String email,
            String matricula,
            String rol,
            String passwordPlano
    ) {

        String rolUpper = rol != null ? rol.trim().toUpperCase() : "";
        if (!rolUpper.equals("DOCTOR") && !rolUpper.equals("ENFERMERA")) {
            throw new CampoInvalidoException("rol", "debe ser DOCTOR o ENFERMERA");
        }

        // username base: apellido + inicial del nombre
        String usernameBase = generarUsernameBase(nombre, apellido);
        String usernameFinal = generarUsernameUnico(usernameBase);

        String passwordHash = encoder.encode(passwordPlano);

        Usuario usuario = new Usuario(usernameFinal, passwordHash);

        Persona persona;
        if (rolUpper.equals("DOCTOR")) {
            // constructor: Doctor(String nombre, String apellido, String cuil, String email, String matricula, Usuario usuario)
            Doctor doctor = new Doctor(
                    nombre,
                    apellido,
                    cuil,
                    email,
                    matricula,
                    usuario
            );
            persona = doctor;
        } else {
            // constructor: Enfermera(String cuil, String nombre, String apellido, String matricula, String email, Usuario usuario)
            Enfermera enfermera = new Enfermera(
                    cuil,
                    nombre,
                    apellido,
                    matricula,
                    email,
                    usuario
            );
            persona = enfermera;
        }

        personalRepo.save(persona);

        return new PersonalRegistradoDTO(
                cuil,
                nombre,
                apellido,
                rolUpper,
                usernameFinal
        );
    }
    private String generarUsernameBase(String nombre, String apellido) {
        String nom = nombre == null ? "" : nombre.trim().toLowerCase();
        String ape = apellido == null ? "" : apellido.trim().toLowerCase();

        String inicial = nom.isEmpty() ? "" : nom.substring(0, 1);
        String base = (ape + inicial)
                .replace(" ", "")
                .replace("ñ", "n");

        return base;
    }

    private String generarUsernameUnico(String base) {
        String candidate = base;
        int i = 1;


        while (personalRepo.findByUsername(candidate).isPresent()) {
            candidate = base + i;
            i++;
        }

        return candidate;
    }
}
