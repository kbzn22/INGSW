// src/main/java/com/grupo1/ingsw_app/persistence/IngresoRepository.java
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class IngresoRepository implements IIngresoRepository {

    private final JdbcTemplate jdbc;

    public IngresoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- SQLs

    private static final String SQL_UPSERT = """
        INSERT INTO ingreso (
            id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
            descripcion, fecha_ingreso,
            temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
        ) VALUES (?, ?, ?, ?, ?::estado_ingreso,
                  ?, ?,
                  ?, ?, ?, ?, ?)
        ON CONFLICT (id) DO UPDATE SET
            cuil_paciente     = EXCLUDED.cuil_paciente,
            cuil_enfermera    = EXCLUDED.cuil_enfermera,
            nivel_emergencia  = EXCLUDED.nivel_emergencia,
            estado_ingreso    = EXCLUDED.estado_ingreso,
            descripcion       = EXCLUDED.descripcion,
            fecha_ingreso     = EXCLUDED.fecha_ingreso,
            temperatura       = EXCLUDED.temperatura,
            frec_cardiaca     = EXCLUDED.frec_cardiaca,
            frec_respiratoria = EXCLUDED.frec_respiratoria,
            sistolica         = EXCLUDED.sistolica,
            diastolica        = EXCLUDED.diastolica
        """;

    private static final String SQL_EXISTS =
            "SELECT 1 FROM ingreso WHERE id = ? LIMIT 1";

    private static final String SQL_TRUNCATE =
            "TRUNCATE TABLE ingreso";

    private static final String SQL_FIND_PENDIENTE = """
        SELECT
          id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
          descripcion, fecha_ingreso,
          temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
        FROM ingreso
        WHERE estado_ingreso = 'PENDIENTE'
        ORDER BY fecha_ingreso ASC
        """;
    private static final String SQL_FIND_BY_ID = """
        SELECT
          id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
          descripcion, fecha_ingreso,
          temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
        FROM ingreso
        WHERE id = ?
        """;

    private static final String SQL_FIND_BY_ESTADO = """
        SELECT
          id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
          descripcion, fecha_ingreso,
          temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
        FROM ingreso
        WHERE estado_ingreso = ?
        ORDER BY fecha_ingreso ASC
        """;

    private static final String SQL_FIND_EN_PROCESO_FIRST = """
        SELECT
          id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
          descripcion, fecha_ingreso,
          temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
        FROM ingreso
        WHERE estado_ingreso = 'EN_PROCESO'
        ORDER BY fecha_ingreso ASC
        LIMIT 1
        """;
    private static final String SQL_COUNT_BY_ESTADO =
            "SELECT COUNT(*) FROM ingreso WHERE estado_ingreso = ?";

    private static final String SQL_FIND_EN_ATENCION = """
    SELECT
      id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
      descripcion, fecha_ingreso,
      temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
    FROM ingreso
    WHERE estado_ingreso = 'EN_PROCESO'
    ORDER BY fecha_ingreso ASC
    LIMIT 1
    """;


    // ---------- RowMapper: fila -> Ingreso (dominio)

    private final RowMapper<Ingreso> mapper = (rs, rowNum) -> {
        UUID id = rs.getObject("id", UUID.class);

        String cuilPac = rs.getString("cuil_paciente");
        String cuilEnf = rs.getString("cuil_enfermera");

        int nivelNum = rs.getInt("nivel_emergencia");
        String estadoStr = rs.getString("estado_ingreso");
        String desc = rs.getString("descripcion");

        Timestamp ts = rs.getTimestamp("fecha_ingreso");
        LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : null;

        // 🔹 TODOS los NUMERIC como BigDecimal
        BigDecimal tempBd = rs.getBigDecimal("temperatura");
        BigDecimal fcBd   = rs.getBigDecimal("frec_cardiaca");
        BigDecimal frBd   = rs.getBigDecimal("frec_respiratoria");
        BigDecimal sisBd  = rs.getBigDecimal("sistolica");
        BigDecimal diaBd  = rs.getBigDecimal("diastolica");

        // 🔹 Convertimos a tus value objects
        Temperatura temp = null;
        if (tempBd != null) {
            temp = new Temperatura(tempBd.floatValue());
        }

        FrecuenciaCardiaca fc = null;
        if (fcBd != null) {
            fc = new FrecuenciaCardiaca(fcBd.doubleValue());
        }

        FrecuenciaRespiratoria fr = null;
        if (frBd != null) {
            fr = new FrecuenciaRespiratoria(frBd.doubleValue());
        }

        TensionArterial ta = null;
        if (sisBd != null && diaBd != null) {
            ta = new TensionArterial(sisBd.doubleValue(), diaBd.doubleValue());
        }

        // Por ahora reconstruyo Paciente solo con cuil (para la cola)
        Paciente paciente = new Paciente(cuilPac, "");  // si querés, esto después lo cambiamos a repo
        Enfermera enfermera = null;                     // lo mismo con cuilEnf

        Ingreso ingreso = new Ingreso(paciente, enfermera, NivelEmergencia.fromNumero(nivelNum));
        ingreso.setId(id);
        ingreso.setEstadoIngreso(EstadoIngreso.valueOf(estadoStr));
        ingreso.setDescripcion(desc);
        ingreso.setFechaIngreso(fecha);
        ingreso.setTemperatura(temp);
        ingreso.setFrecuenciaCardiaca(fc);
        ingreso.setFrecuenciaRespiratoria(fr);
        ingreso.setTensionArterial(ta);

        return ingreso;
    };

    // ---------- Implementación de IIngresoRepository

    @Override
    public void save(Ingreso ingreso) {
        if (ingreso.getId() == null) {
            ingreso.setId(UUID.randomUUID());
        }

        String cuilPaciente = ingreso.getPaciente() != null
                ? ingreso.getPaciente().getCuil().getValor()
                : null;

        String cuilEnfermera = ingreso.getEnfermera() != null
                ? ingreso.getEnfermera().getCuil().getValor()
                : null;

        Integer nivelNum = ingreso.getNivelEmergencia() != null
                ? ingreso.getNivelEmergencia().getNumero()
                : null;

        String estado = ingreso.getEstadoIngreso() != null
                ? ingreso.getEstadoIngreso().name()
                : EstadoIngreso.PENDIENTE.name();

        LocalDateTime fecha = ingreso.getFechaIngreso() != null
                ? ingreso.getFechaIngreso()
                : LocalDateTime.now();

        Double temperatura = ingreso.getTemperatura() != null
                ? (double) ingreso.getTemperatura().getTemperatura()
                : null;

        Double fc = ingreso.getFrecuenciaCardiaca() != null
                ? ingreso.getFrecuenciaCardiaca().getValor()
                : null;

        Double fr = ingreso.getFrecuenciaRespiratoria() != null
                ? ingreso.getFrecuenciaRespiratoria().getValor()
                : null;

        Double sistolica = null;
        Double diastolica = null;
        if (ingreso.getTensionArterial() != null) {
            sistolica = ingreso.getTensionArterial().getSistolica().getValor();
            diastolica = ingreso.getTensionArterial().getDiastolica().getValor();
        }

        jdbc.update(SQL_UPSERT,
                ingreso.getId(),
                cuilPaciente,
                cuilEnfermera,
                nivelNum,
                estado,
                ingreso.getDescripcion(),
                Timestamp.valueOf(fecha),
                temperatura,
                fc,
                fr,
                sistolica,
                diastolica
        );
    }

    @Override
    public boolean existsById(String id) {
        UUID uuid = UUID.fromString(id);
        return Boolean.TRUE.equals(
                jdbc.queryForObject(SQL_EXISTS, Boolean.class, uuid)
        );
    }

    @Override
    public void clear() {
        jdbc.execute(SQL_TRUNCATE);
    }

    @Override
    public List<Ingreso> findByEstadoPendiente() {
        String sql = """
            SELECT
              id,
              cuil_paciente,
              cuil_enfermera,
              nivel_emergencia,
              estado_ingreso,
              descripcion,
              fecha_ingreso,
              temperatura,
              frec_cardiaca,
              frec_respiratoria,
              sistolica,
              diastolica
            FROM ingreso
            WHERE estado_ingreso = 'PENDIENTE'
            ORDER BY fecha_ingreso ASC
            """;

        return jdbc.query(sql, mapper);
    }
    @Override
    public Optional<Ingreso> findById(UUID id) {
        List<Ingreso> resultados = jdbc.query(SQL_FIND_BY_ID, mapper, id);
        return resultados.stream().findFirst();
    }

    @Override
    public List<Ingreso> findByEstado(EstadoIngreso estado) {
        return jdbc.query(SQL_FIND_BY_ESTADO, mapper, estado.name());
    }

    @Override
    public Optional<Ingreso> findFirstEnProceso() {
        List<Ingreso> resultados = jdbc.query(SQL_FIND_EN_PROCESO_FIRST, mapper);
        return resultados.stream().findFirst();
    }
    @Override
    public int countByEstado(EstadoIngreso estado) {
        return jdbc.queryForObject(SQL_COUNT_BY_ESTADO, Integer.class, estado.name());
    }

    @Override
    public Optional<Ingreso> findEnAtencionActual() {
        return jdbc.query(SQL_FIND_EN_ATENCION, mapper)
                .stream().findFirst();
    }


}
