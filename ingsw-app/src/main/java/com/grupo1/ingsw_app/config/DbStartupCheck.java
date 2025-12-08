package com.grupo1.ingsw_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("!test")
public class DbStartupCheck {

    @Bean
    CommandLineRunner checkDbOnStartup(JdbcTemplate jdbc) {
        return args -> {
            String version = jdbc.queryForObject("SELECT version()", String.class);
            System.out.println("Conectado a PostgreSQL: " + version);
        };
    }
}
