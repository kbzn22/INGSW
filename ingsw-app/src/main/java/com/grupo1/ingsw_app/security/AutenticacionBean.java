package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.persistence.PersonalRepository;
import com.grupo1.ingsw_app.service.AutenticacionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AutenticacionBean {

    @Bean
    PersonalRepository personalRepository() {
        return new PersonalRepository();
    }

    @Bean
    AutenticacionService autenticacionService(
            Sesion sesion,
            PasswordEncoder encoder,
            PersonalRepository personal) {
        AutenticacionService auth = new AutenticacionService(personal, sesion,encoder);
        return auth;
    }
}
