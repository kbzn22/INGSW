
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.security.Sesion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class SesionRepository implements ISesionRepository {

    private final JdbcTemplate jdbc;
    private final PersonalRepository personalRepo;

    public SesionRepository(JdbcTemplate jdbc, PersonalRepository personalRepo) {
        this.jdbc = jdbc;
        this.personalRepo = personalRepo;
    }

    private static final String SQL_UPSERT = """
        INSERT INTO sesion (id, cuil_persona, expires_at)
        VALUES (?, ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            cuil_persona = EXCLUDED.cuil_persona,
            expires_at   = EXCLUDED.expires_at
        """;

    private static final String SQL_FIND = """
        SELECT id, cuil_persona, expires_at
        FROM sesion
        WHERE id = ?
        """;

    private static final String SQL_DELETE = """
        DELETE FROM sesion
        WHERE id = ?
        """;
    private static final String SQL_DELETE_BY_CUIL = """
        DELETE FROM sesion
        WHERE cuil_persona = ?
        """;

    @Override
    public void save(Sesion s) {
        System.out.println("Sesion ID: " + s.getId());
        jdbc.update(SQL_UPSERT,
                s.getId(),
                s.getPersona().getCuil().getValor(),
                Timestamp.from(s.getExpiresAt())
        );
    }

    @Override
    public Optional<Sesion> find(String id) {
        List<Sesion> list = jdbc.query(SQL_FIND, (rs, rowNum) -> {

            String cuil    = rs.getString("cuil_persona");
            Instant expires = rs.getTimestamp("expires_at").toInstant();

            var persona = personalRepo.findByCuil(cuil).orElse(null);
            if (persona == null) return null;


            String username;
            if (persona instanceof Doctor d && d.getUsuario() != null) {
                username = d.getUsuario().getUsuario();
            } else if (persona instanceof Enfermera e && e.getUsuario() != null) {
                username = e.getUsuario().getUsuario();
            } else {
                return null;
            }

            return Sesion.restaurar(id, username, persona, expires);
        }, id);

        if (list.isEmpty() || list.get(0) == null) return Optional.empty();

        Sesion s = list.get(0);
        if (s.isExpired()) {
            delete(id);
            return Optional.empty();
        }

        return Optional.of(s);
    }
    @Override
    public void delete(String id) {
        System.out.println("DEBUG delete sesion, id=[" + id + "] len=" + id.length());
        int rows = jdbc.update(SQL_DELETE, id);
        System.out.println("DEBUG filas borradas = " + rows);
    }
    @Override
    public void deleteByPersona(String cuilPersona) {
        jdbc.update(SQL_DELETE_BY_CUIL, cuilPersona);
    }

}
