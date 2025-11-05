package com.grupo1.ingsw_app.security;

import com.grupo1.ingsw_app.persistance.DoctorRepository;
import com.grupo1.ingsw_app.persistance.EnfermeraRepository;
import com.grupo1.ingsw_app.persistance.SesionRepository;
import com.grupo1.ingsw_app.persistance.UsuarioRepository;
import com.grupo1.ingsw_app.service.AutenticacionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AutenticacionBean {

    @Bean
    UsuarioRepository userRepository(){ return new UsuarioRepository(); }
    @Bean
    SesionRepository sessionRepository(){ return new SesionRepository(); }
    @Bean
    DoctorRepository doctorRepository(){return new DoctorRepository();}
    @Bean
    EnfermeraRepository enfermeraRepository () {return new EnfermeraRepository();}
    @Bean
    AutenticacionService authenticationService(UsuarioRepository userRepo,
                                               SesionRepository sessionRepo,
                                               PasswordEncoder encoder,
                                               DoctorRepository doctorRepo,
                                               EnfermeraRepository enfermeraRepo) {
        AutenticacionService auth = new AutenticacionService(userRepo, sessionRepo, encoder,doctorRepo,enfermeraRepo);

        // demo seed (los del feature)
        auth.register("delvallem","contr123","20-30574930-4",java.util.Set.of("USER"));
        auth.register("juareze","contr456","20-12547856-4", java.util.Set.of("USER"));
        return auth;
    }
}
