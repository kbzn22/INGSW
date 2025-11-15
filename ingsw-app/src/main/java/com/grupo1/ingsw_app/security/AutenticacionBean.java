package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.persistence.IPersonalRepository;
import com.grupo1.ingsw_app.persistence.ISesionRepository;
import com.grupo1.ingsw_app.service.AutenticacionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AutenticacionBean {

    @Bean
    AutenticacionService autenticacionService(
            Sesion sesion,
            PasswordEncoder encoder,
            IPersonalRepository personal,
            ISesionRepository sesionRepository) {
        return new AutenticacionService(personal, sesion, encoder,sesionRepository);
    }
}
