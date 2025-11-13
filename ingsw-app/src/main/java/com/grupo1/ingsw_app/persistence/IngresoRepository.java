// src/main/java/com/grupo1/ingsw_app/persistence/IngresoRepository.java
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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

    // ---------- RowMapper: fila -> Ingreso (dominio)

    private final RowMapper<Ingreso> mapper = (ResultSet rs, int rowNum) -> {
        UUID id = (UUID) rs.getObject("id");
        int nivelNum = rs.getInt("nivel_emergencia");
        NivelEmergencia nivel = NivelEmergencia.fromNumero(nivelNum);

        EstadoIngreso estado = EstadoIngreso.valueOf(rs.getString("estado_ingreso"));
        LocalDateTime fecha = rs.getTimestamp("fecha_ingreso").toLocalDateTime();
        String descripcion = rs.getString("descripcion");

        // No hidratamos Paciente/Enfermera acá: se pueden resolver por CUIL en otros repos
        Ingreso ingreso = new Ingreso(null, null, nivel);
        ingreso.setId(id);
        ingreso.setEstadoIngreso(estado);
        ingreso.setFechaIngreso(fecha);
        ingreso.setDescripcion(descripcion);

        // Temperatura
        var temp = rs.getBigDecimal("temperatura");
        if (temp != null) {
            ingreso.setTemperatura(new Temperatura(temp.floatValue()));
        }

        // Frecuencias
        Double fc = rs.getObject("frec_cardiaca", Double.class);
        if (fc != null) ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(fc));

        Double fr = rs.getObject("frec_respiratoria", Double.class);
        if (fr != null) ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(fr));

        Double sistolica = rs.getObject("sistolica", Double.class);
        Double diastolica = rs.getObject("diastolica", Double.class);
        if (sistolica != null && diastolica != null) {
            ingreso.setTensionArterial(new TensionArterial(sistolica, diastolica));
        }

        // Si tu Ingreso luego necesita saber los CUIL, podés agregar campos extra
        // o resolver en otro repo usando cuil_paciente / cuil_enfermera.

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
        return jdbc.query(SQL_FIND_PENDIENTE, mapper);
    }
}
