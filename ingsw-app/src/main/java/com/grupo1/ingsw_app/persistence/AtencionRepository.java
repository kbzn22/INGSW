// src/main/java/com/grupo1/ingsw_app/persistence/AtencionRepository.java
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Atencion;
import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Ingreso;
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

    public AtencionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
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

    private final RowMapper<Atencion> mapper = (rs, rowNum) -> {
        UUID id          = rs.getObject("id", UUID.class);
        UUID ingresoId   = rs.getObject("ingreso_id", UUID.class);
        String cuilDoc   = rs.getString("cuil_doctor");
        String informe   = rs.getString("informe");
        Timestamp ts     = rs.getTimestamp("fecha_atencion");
        LocalDateTime fa = ts != null ? ts.toLocalDateTime() : null;

        // Reconstruyo objetos mínimos para no acoplar a toda la agregación
        Doctor doctor = new Doctor(cuilDoc, null, null, null, null); // ajustá al constructor que tengas
        Ingreso ingreso = new Ingreso();                             // idem, después lo podés hidratar mejor
        ingreso.setId(ingresoId);

        Atencion atencion = new Atencion(doctor, ingreso);
        atencion.setId(id);
        atencion.setInforme(informe);
        atencion.setFechaAtencion(fa);

        return atencion;
    };

    @Override
    public void save(Atencion atencion) {
        if (atencion.getId() == null) {
            atencion.setId(UUID.randomUUID());
        }


        if (atencion.getIngreso() == null || atencion.getIngreso().getId() == null) {
            throw new IllegalStateException("La atención no tiene ingreso o el ingreso no tiene ID");
        }

        if (atencion.getDoctor() == null || atencion.getDoctor().getCuil() == null) {
            throw new IllegalStateException("La atención no tiene médico o el médico no tiene CUIL");
        }

        if (atencion.getDoctor().getCuil().getValor() == null) {
            throw new IllegalStateException("El CUIL del médico es nulo");
        }

        if (atencion.getFechaAtencion() == null) {
            // si por alguna razón no se seteó, usá ahora
            atencion.setFechaAtencion(LocalDateTime.now());
        }

        jdbc.update(SQL_UPSERT,
                atencion.getId(),
                atencion.getIngreso().getId(),
                atencion.getDoctor().getCuil().getValor(),
                atencion.getInforme(),
                Timestamp.valueOf(atencion.getFechaAtencion())
        );
    }

    @Override
    public Optional<Atencion> findByIngresoId(UUID ingresoId) {
        return jdbc.query(SQL_FIND_BY_INGRESO, mapper, ingresoId)
                .stream().findFirst();
    }
}
