
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PersonalRepository implements IPersonalRepository {

    private final JdbcTemplate jdbc;

    public PersonalRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }



    private static final String SQL_UPSERT_PERSONAL = """
        INSERT INTO personal (
            cuil, nombre, apellido, email, matricula, tipo
        ) VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT (cuil) DO UPDATE SET
            nombre   = EXCLUDED.nombre,
            apellido = EXCLUDED.apellido,
            email    = EXCLUDED.email,
            matricula= EXCLUDED.matricula,
            tipo     = EXCLUDED.tipo
        """;

    private static final String SQL_UPSERT_USUARIO = """
        INSERT INTO usuario_personal (username, password_hash, cuil_personal)
        VALUES (?, ?, ?)
        ON CONFLICT (username) DO UPDATE SET
            password_hash = EXCLUDED.password_hash,
            cuil_personal = EXCLUDED.cuil_personal
        """;

    private static final String SQL_FIND_BY_USERNAME = """
        SELECT
          p.cuil, p.nombre, p.apellido, p.email, p.matricula, p.tipo,
          u.username, u.password_hash
        FROM personal p
        JOIN usuario_personal u ON u.cuil_personal = p.cuil
        WHERE u.username = ?
        """;

    private static final String SQL_FIND_BY_CUIL = """
        SELECT
          p.cuil, p.nombre, p.apellido, p.email, p.matricula, p.tipo,
          u.username, u.password_hash
        FROM personal p
        LEFT JOIN usuario_personal u ON u.cuil_personal = p.cuil
        WHERE p.cuil = ?
        """;

    private static final String SQL_FIND_BY_MATRICULA = """
        SELECT
          p.cuil, p.nombre, p.apellido, p.email, p.matricula, p.tipo,
          u.username, u.password_hash
        FROM personal p
        LEFT JOIN usuario_personal u ON u.cuil_personal = p.cuil
        WHERE p.matricula = ?
        """;

    private static final String SQL_FIND_ALL_DOCTORES = """
        SELECT
          p.cuil, p.nombre, p.apellido, p.email, p.matricula, p.tipo,
          u.username, u.password_hash
        FROM personal p
        LEFT JOIN usuario_personal u ON u.cuil_personal = p.cuil
        WHERE p.tipo = 'DOCTOR'
        """;

    private static final String SQL_FIND_ALL_ENFERMERAS = """
        SELECT
          p.cuil, p.nombre, p.apellido, p.email, p.matricula, p.tipo,
          u.username, u.password_hash
        FROM personal p
        LEFT JOIN usuario_personal u ON u.cuil_personal = p.cuil
        WHERE p.tipo = 'ENFERMERA'
        """;


    private final RowMapper<Persona> mapperPersona = (rs, rowNum) -> {
        String cuil       = rs.getString("cuil");
        String nombre     = rs.getString("nombre");
        String apellido   = rs.getString("apellido");
        String email      = rs.getString("email");
        String matricula  = rs.getString("matricula");
        String tipo       = rs.getString("tipo");

        String username   = rs.getString("username");
        String password   = rs.getString("password_hash");

        Usuario usuario = null;
        if (username != null && password != null) {
            usuario = new Usuario(username, password);
        }

        if ("ENFERMERA".equalsIgnoreCase(tipo)) {
            return new Enfermera(cuil, nombre, apellido, matricula, email, usuario);
        } else if ("DOCTOR".equalsIgnoreCase(tipo)) {
            return new Doctor(nombre, apellido, cuil, email, matricula, usuario);
        } else {
            throw new IllegalStateException("Tipo de personal desconocido en DB: " + tipo);
        }
    };



    @Override
    public void save(Persona persona) {
        String tipo;
        String matricula = null;
        Usuario usuario = null;

        if (persona instanceof Enfermera e) {
            tipo = "ENFERMERA";
            matricula = e.getMatricula();
            usuario = e.getUsuario();
        } else if (persona instanceof Doctor d) {
            tipo = "DOCTOR";
            matricula = d.getMatricula();
            usuario = d.getUsuario();
        } else {
            throw new IllegalArgumentException("Solo se admite Doctor o Enfermera en PersonalRepository");
        }

        String cuil = persona.getCuil().getValor();


        jdbc.update(SQL_UPSERT_PERSONAL,
                cuil,
                persona.getNombre(),
                persona.getApellido(),
                persona.getEmail(),
                matricula,
                tipo
        );


        if (usuario != null) {
            jdbc.update(SQL_UPSERT_USUARIO,
                    usuario.getUsuario(),
                    usuario.getPassword(),
                    cuil
            );
        }
    }

    @Override
    public Optional<Persona> findByUsername(String username) {
        List<Persona> lista = jdbc.query(SQL_FIND_BY_USERNAME, mapperPersona, username);
        if (lista.isEmpty()) return Optional.empty();
        return Optional.of(lista.get(0));
    }

    @Override
    public Optional<Persona> findByMatricula(String matricula) {
        List<Persona> lista = jdbc.query(SQL_FIND_BY_MATRICULA, mapperPersona, matricula);
        if (lista.isEmpty()) return Optional.empty();
        return Optional.of(lista.get(0));
    }

    @Override
    public Optional<Persona> findByCuil(String cuil) {
        List<Persona> lista = jdbc.query(SQL_FIND_BY_CUIL, mapperPersona, cuil);
        if (lista.isEmpty()) return Optional.empty();
        return Optional.of(lista.get(0));
    }

    @Override
    public Optional<Doctor> findDoctorByUsername(String username) {
        return findByUsername(username)
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast);
    }

    @Override
    public Optional<Enfermera> findEnfermeraByUsername(String username) {
        return findByUsername(username)
                .filter(Enfermera.class::isInstance)
                .map(Enfermera.class::cast);
    }

    @Override
    public List<Doctor> findAllDoctors() {
        return jdbc.query(SQL_FIND_ALL_DOCTORES, mapperPersona).stream()
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast)
                .toList();
    }

    @Override
    public List<Enfermera> findAllEnfermeras() {
        return jdbc.query(SQL_FIND_ALL_ENFERMERAS, mapperPersona).stream()
                .filter(Enfermera.class::isInstance)
                .map(Enfermera.class::cast)
                .toList();
    }
}
