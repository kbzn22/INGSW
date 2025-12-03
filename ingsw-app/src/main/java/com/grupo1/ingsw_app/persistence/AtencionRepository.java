// src/main/java/com/grupo1/ingsw_app/persistence/AtencionRepository.java
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Atencion;
import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.Persona;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AtencionRepository implements IAtencionRepository {

    private final JdbcTemplate jdbc;
    private final IPersonalRepository personalRepository;
    private final IIngresoRepository ingresoRepository;

    public AtencionRepository(JdbcTemplate jdbc,
                              PersonalRepository personalRepository,
                              IIngresoRepository ingresoRepository) {
        this.jdbc = jdbc;
        this.personalRepository = personalRepository;
        this.ingresoRepository = ingresoRepository;
    }

    private static final String SQL_UPSERT = """
        INSERT INTO atencion (
            id, ingreso_id, cuil_doctor, informe, fecha_atencion
        ) VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            ingreso_id     = EXCLUDED.ingreso_id,
            cuil_doctor    = EXCLUDED.cuil_doctor,
            informe        = EXCLUDED.informe,
            fecha_atencion = EXCLUDED.fecha_atencion
        """;

    private static final String SQL_FIND_BY_INGRESO = """
        SELECT id, ingreso_id, cuil_doctor, informe, fecha_atencion
        FROM atencion
        WHERE ingreso_id = ?
        """;

    // ===== RowMapper usando PersonalRepository para hidratar el Doctor =====
    private RowMapper<Atencion> mapper() {
        return (rs, rowNum) -> {
            UUID id        = rs.getObject("id", UUID.class);
            UUID ingresoId = rs.getObject("ingreso_id", UUID.class);
            String cuilDoc = rs.getString("cuil_doctor");
            String informe = rs.getString("informe");
            Timestamp ts   = rs.getTimestamp("fecha_atencion");
            LocalDateTime fa = ts != null ? ts.toLocalDateTime() : null;

            // reconstruyo Ingreso mínimo
            Ingreso ingreso = ingresoRepository.findById(ingresoId).orElse(null);

            // Doctor desde PersonalRepository
            Doctor doctor = null;
            if (cuilDoc != null) {
                Optional<Persona> personaOpt = personalRepository.findByCuil(cuilDoc);
                if (personaOpt.isPresent() && personaOpt.get() instanceof Doctor d) {
                    doctor = d;
                }
            }

            Atencion atencion = new Atencion(doctor, ingreso);
            atencion.setId(id);
            atencion.setInforme(informe);
            atencion.setFechaAtencion(fa);

            return atencion;
        };
    }

    @Override
    public void save(Atencion atencion) {
        if (atencion.getId() == null) {
            atencion.setId(UUID.randomUUID());
        }
        if (atencion.getFechaAtencion() == null) {
            atencion.setFechaAtencion(LocalDateTime.now());
        }

        jdbc.update(SQL_UPSERT,
                atencion.getId(),
                atencion.getIngreso().getId(),             // ingreso_id NOT NULL
                atencion.getDoctor().getCuil().getValor(), // cuil_doctor NOT NULL
                atencion.getInforme(),
                Timestamp.valueOf(atencion.getFechaAtencion())
        );
    }

    @Override
    public Optional<Atencion> findByIngresoId(UUID ingresoId) {
        return jdbc.query(SQL_FIND_BY_INGRESO, mapper(), ingresoId)
                .stream().findFirst();
    }
}
