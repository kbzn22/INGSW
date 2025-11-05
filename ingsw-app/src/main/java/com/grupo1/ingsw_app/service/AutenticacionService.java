package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.Usuario;
import com.grupo1.ingsw_app.persistance.DoctorRepository;
import com.grupo1.ingsw_app.persistance.EnfermeraRepository;
import com.grupo1.ingsw_app.persistance.SesionRepository;
import com.grupo1.ingsw_app.persistance.UsuarioRepository;
import com.grupo1.ingsw_app.security.SesionActual;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Set;

public class AutenticacionService {

        private final UsuarioRepository userRepo;
        private final SesionRepository sesionRepo;
        private final PasswordEncoder encoder;
        private final DoctorRepository doctorRepo;
        private final EnfermeraRepository enfermeraRepo;
        private final SecureRandom random = new SecureRandom();


        public AutenticacionService(UsuarioRepository userRepo, SesionRepository sesionRepo, PasswordEncoder encoder,DoctorRepository doctorRepo, EnfermeraRepository enfermeraRepo) {
            this.userRepo = userRepo;
            this.sesionRepo = sesionRepo;
            this.encoder = encoder;
            this.doctorRepo=doctorRepo;
            this.enfermeraRepo=enfermeraRepo;

        }


        public void register(String username, String rawPassword, String cuil, Set<String> roles){
            String hash = encoder.encode(rawPassword);
            userRepo.save(new Usuario(username, hash));
        }

        public String login(String username, String rawPassword){
            Usuario u = userRepo.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("El usuario no existe"));

            if (!encoder.matches(rawPassword, u.getPassword()))
                throw new IllegalArgumentException("La contraseña es incorrecta");

            String sid = newSessionId();
            SesionActual s = new SesionActual(sid, u.getUsuario(), Instant.now().plus(2, ChronoUnit.HOURS));
            sesionRepo.save(s);
            return sid; // este es el valor que va en el cookie
        }

        public void logout(String sessionId){
            sesionRepo.delete(sessionId);
        }

        public Usuario requireSession(String sessionId){
            SesionActual s = sesionRepo.find(sessionId)
                    .orElseThrow(() -> new IllegalStateException("Sesión inválida o expirada"));
            return userRepo.findByUsername(s.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Usuario de sesión inexistente"));
        }

        private String newSessionId(){
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }

}

