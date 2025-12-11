package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Atencion;
import com.grupo1.ingsw_app.domain.Doctor;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.dtos.AtencionLogDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private static final String SQL_LOG_ATENCIONES_BASE = """
    SELECT
      a.id                AS atencion_id,
      a.ingreso_id        AS ingreso_id,
      a.cuil_doctor       AS cuil_doctor,
      a.informe           AS informe,
      a.fecha_atencion    AS fecha_atencion,
      i.cuil_paciente     AS cuil_paciente,
      i.cuil_enfermera    AS cuil_enfermera,
      i.nivel_emergencia  AS nivel_emergencia,
      i.estado_ingreso    AS estado_ingreso,
      i.fecha_ingreso     AS fecha_ingreso
    FROM atencion a
    LEFT JOIN ingreso i ON a.ingreso_id = i.id
    WHERE a.fecha_atencion BETWEEN ? AND ?
    """;
    private static final String SQL_FIND_LOG_BASE = """
        SELECT id, ingreso_id, cuil_doctor, informe, fecha_atencion
        FROM atencion
        WHERE 1=1
        """;
    private static final String SQL_FIND_BY_ID = """
        SELECT id, ingreso_id, cuil_doctor, informe, fecha_atencion
        FROM atencion
        WHERE id = ?
        """;


    private RowMapper<Atencion> mapper() {
        return (rs, rowNum) -> {
            UUID id        = rs.getObject("id", UUID.class);
            UUID ingresoId = rs.getObject("ingreso_id", UUID.class);
            String cuilDoc = rs.getString("cuil_doctor");
            String informe = rs.getString("informe");
            Timestamp ts   = rs.getTimestamp("fecha_atencion");
            LocalDateTime fa = ts != null ? ts.toLocalDateTime() : null;


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
                atencion.getIngreso().getId(),
                atencion.getDoctor().getCuil().getValor(),
                atencion.getInforme(),
                Timestamp.valueOf(atencion.getFechaAtencion())
        );
    }

    @Override
    public Optional<Atencion> findByIngresoId(UUID ingresoId) {
        return jdbc.query(SQL_FIND_BY_INGRESO, mapper(), ingresoId)
                .stream().findFirst();
    }
    public List<AtencionLogDTO> findLogs(
            LocalDateTime desde,
            LocalDateTime hasta,
            String cuilDoctor,
            String cuilPaciente
    ) {
        StringBuilder sql = new StringBuilder(SQL_LOG_ATENCIONES_BASE);
        List<Object> params = new ArrayList<>();
        params.add(Timestamp.valueOf(desde));
        params.add(Timestamp.valueOf(hasta));

        if (cuilDoctor != null && !cuilDoctor.isBlank()) {
            sql.append(" AND a.cuil_doctor = ?");
            params.add(cuilDoctor);
        }
        if (cuilPaciente != null && !cuilPaciente.isBlank()) {
            sql.append(" AND i.cuil_paciente = ?");
            params.add(cuilPaciente);
        }

        sql.append(" ORDER BY a.fecha_atencion ASC");

        return jdbc.query(sql.toString(), (rs, rowNum) -> new AtencionLogDTO(
                rs.getObject("atencion_id", java.util.UUID.class),
                rs.getObject("ingreso_id", java.util.UUID.class),
                rs.getString("cuil_doctor"),
                rs.getString("informe"),
                rs.getTimestamp("fecha_atencion").toLocalDateTime(),
                rs.getString("cuil_paciente"),
                rs.getString("cuil_enfermera"),
                rs.getObject("nivel_emergencia") != null ? rs.getInt("nivel_emergencia") : null,
                rs.getString("estado_ingreso"),
                rs.getTimestamp("fecha_ingreso") != null
                        ? rs.getTimestamp("fecha_ingreso").toLocalDateTime()
                        : null
        ), params.toArray());
    }
    @Override
    public List<Atencion> findForLog(LocalDateTime desde,
                                     LocalDateTime hasta,
                                     String cuilDoctor) {

        StringBuilder sql = new StringBuilder(SQL_FIND_LOG_BASE);
        List<Object> params = new ArrayList<>();

        if (desde != null) {
            sql.append(" AND fecha_atencion >= ? ");
            params.add(Timestamp.valueOf(desde));
        }
        if (hasta != null) {
            sql.append(" AND fecha_atencion <= ? ");
            params.add(Timestamp.valueOf(hasta));
        }
        if (cuilDoctor != null && !cuilDoctor.isBlank()) {
            sql.append(" AND cuil_doctor = ? ");
            params.add(cuilDoctor);
        }

        sql.append(" ORDER BY fecha_atencion ASC ");

        return jdbc.query(sql.toString(), mapper(), params.toArray());
    }
    @Override
    public Optional<Atencion> findById(UUID id) {
        return jdbc.query(SQL_FIND_BY_ID, mapper(), id)
                .stream().findFirst();
    }
}
