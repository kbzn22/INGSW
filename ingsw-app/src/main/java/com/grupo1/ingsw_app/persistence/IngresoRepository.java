// src/main/java/com/grupo1/ingsw_app/persistence/IngresoRepository.java
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.IngresoDetalleDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class IngresoRepository implements IIngresoRepository {

    private final JdbcTemplate jdbc;
    private final IPersonalRepository personalRepository;

    public IngresoRepository(JdbcTemplate jdbc, PersonalRepository personalRepository) {
        this.jdbc = jdbc;
        this.personalRepository=personalRepository;
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
    WHERE estado_ingreso = ?::estado_ingreso
    ORDER BY nivel_emergencia ASC, fecha_ingreso ASC
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
            "SELECT COUNT(*) FROM ingreso WHERE estado_ingreso = ?::estado_ingreso";


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
    private static final String SQL_LOG_INGRESOS_BASE = """
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
    WHERE fecha_ingreso BETWEEN ? AND ?
    """;
    private static final String SQL_DETALLES_EXPORT_BASE = """
    SELECT
      i.id                         AS id_ingreso,
      p.cuil                       AS cuil_paciente,
      p.nombre                     AS nombre_paciente,
      p.apellido                   AS apellido_paciente,
      os.nombre                    AS obra_social,
      pa.numero_afiliado          AS numero_afiliado,
      e.cuil                       AS cuil_enfermera,
      e.nombre                     AS nombre_enfermera,
      e.apellido                   AS apellido_enfermera,
      i.nivel_emergencia          AS nivel,
      i.estado_ingreso            AS estado,
      i.descripcion               AS informe,
      i.fecha_ingreso             AS fecha_ingreso,
      i.temperatura,
      i.frec_cardiaca,
      i.frec_respiratoria,
      i.sistolica,
      i.diastolica
    FROM ingreso i
    JOIN paciente p
      ON p.cuil = i.cuil_paciente
    LEFT JOIN paciente_obra_social pa
      ON pa.cuil_paciente = p.cuil
    LEFT JOIN obra_social os
      ON os.codigo = pa.codigo_obra_social
    LEFT JOIN enfermera e
      ON e.cuil = i.cuil_enfermera
    WHERE i.fecha_ingreso BETWEEN ? AND ?
    """;
    private static final String SQL_FIND_LOG_BASE = """
        SELECT
          id, cuil_paciente, cuil_enfermera, nivel_emergencia, estado_ingreso,
          descripcion, fecha_ingreso,
          temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
        FROM ingreso
        WHERE 1=1
        """;




    // ---------- RowMapper: fila -> Ingreso (dominio)

    private RowMapper<Ingreso> mapper() {
        return (rs, rowNum) -> {
            UUID id = rs.getObject("id", UUID.class);

            String cuilPac = rs.getString("cuil_paciente");
            String cuilEnf = rs.getString("cuil_enfermera");

            int nivelNum = rs.getInt("nivel_emergencia");
            String estadoStr = rs.getString("estado_ingreso");
            String desc = rs.getString("descripcion");

            Timestamp ts = rs.getTimestamp("fecha_ingreso");
            LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : null;

            // NUMERIC → objetos
            Temperatura temp = rs.getBigDecimal("temperatura") != null
                    ? new Temperatura(rs.getBigDecimal("temperatura").floatValue())
                    : null;

            FrecuenciaCardiaca fc = rs.getBigDecimal("frec_cardiaca") != null
                    ? new FrecuenciaCardiaca(rs.getBigDecimal("frec_cardiaca").doubleValue())
                    : null;

            FrecuenciaRespiratoria fr = rs.getBigDecimal("frec_respiratoria") != null
                    ? new FrecuenciaRespiratoria(rs.getBigDecimal("frec_respiratoria").doubleValue())
                    : null;

            TensionArterial ta = null;
            if (rs.getBigDecimal("sistolica") != null && rs.getBigDecimal("diastolica") != null) {
                ta = new TensionArterial(
                        rs.getBigDecimal("sistolica").doubleValue(),
                        rs.getBigDecimal("diastolica").doubleValue()
                );
            }

            // Paciente mínimo
            Paciente paciente = new Paciente(cuilPac, "");

            // Enfermera desde PersonalRepository
            Enfermera enfermera = null;
            if (cuilEnf != null) {
                var personaOpt = personalRepository.findByCuil(cuilEnf);
                if (personaOpt.isPresent() && personaOpt.get() instanceof Enfermera e) {
                    enfermera = e;
                }
            }

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
    }


    // ---------- Implementación de IIngresoRepository

    @Override
    public List<Ingreso> findForLog(LocalDateTime desde,
                                    LocalDateTime hasta,
                                    String cuilPaciente,
                                    String cuilEnfermera) {

        StringBuilder sql = new StringBuilder(SQL_FIND_LOG_BASE);
        List<Object> params = new ArrayList<>();

        if (desde != null) {
            sql.append(" AND fecha_ingreso >= ? ");
            params.add(Timestamp.valueOf(desde));
        }
        if (hasta != null) {
            sql.append(" AND fecha_ingreso <= ? ");
            params.add(Timestamp.valueOf(hasta));
        }
        if (cuilPaciente != null && !cuilPaciente.isBlank()) {
            sql.append(" AND cuil_paciente = ? ");
            params.add(cuilPaciente);
        }
        if (cuilEnfermera != null && !cuilEnfermera.isBlank()) {
            sql.append(" AND cuil_enfermera = ? ");
            params.add(cuilEnfermera);
        }

        sql.append(" ORDER BY fecha_ingreso ASC ");

        return jdbc.query(sql.toString(), mapper(), params.toArray());
    }
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

        return jdbc.query(sql, mapper());
    }
    @Override
    public Optional<Ingreso> findById(UUID id) {
        List<Ingreso> resultados = jdbc.query(SQL_FIND_BY_ID, mapper(), id);
        return resultados.stream().findFirst();
    }

    @Override
    public List<Ingreso> findByEstado(EstadoIngreso estado) {
        return jdbc.query(SQL_FIND_BY_ESTADO, mapper(), estado.name());
    }

    @Override
    public Optional<Ingreso> findFirstEnProceso() {
        List<Ingreso> resultados = jdbc.query(SQL_FIND_EN_PROCESO_FIRST, mapper());
        return resultados.stream().findFirst();
    }
    @Override
    public int countByEstado(EstadoIngreso estado) {
        return jdbc.queryForObject(SQL_COUNT_BY_ESTADO, Integer.class, estado.name());
    }

    @Override
    public Optional<Ingreso> findEnAtencionActual() {
        return jdbc.query(SQL_FIND_EN_ATENCION, mapper())
                .stream().findFirst();
    }

    @Override
    public List<IngresoDetalleDTO> findDetallesParaExport(
            LocalDateTime desde,
            LocalDateTime hasta,
            String cuilPaciente,
            String cuilEnfermera
    ) {
        StringBuilder sql = new StringBuilder(SQL_DETALLES_EXPORT_BASE);
        List<Object> params = new ArrayList<>();

        params.add(Timestamp.valueOf(desde));
        params.add(Timestamp.valueOf(hasta));

        if (cuilPaciente != null && !cuilPaciente.isBlank()) {
            sql.append(" AND i.cuil_paciente = ?");
            params.add(cuilPaciente);
        }
        if (cuilEnfermera != null && !cuilEnfermera.isBlank()) {
            sql.append(" AND i.cuil_enfermera = ?");
            params.add(cuilEnfermera);
        }

        sql.append(" ORDER BY i.fecha_ingreso ASC");

        return jdbc.query(sql.toString(), (rs, rowNum) -> {
            UUID idIngreso = rs.getObject("id_ingreso", java.util.UUID.class);

            String cuilPac   = rs.getString("cuil_paciente");
            String nomPac    = rs.getString("nombre_paciente");
            String apePac    = rs.getString("apellido_paciente");
            String obraSoc   = rs.getString("obra_social");
            String nroAfi    = rs.getString("numero_afiliado");

            String cuilEnf   = rs.getString("cuil_enfermera");
            String nomEnf    = rs.getString("nombre_enfermera");
            String apeEnf    = rs.getString("apellido_enfermera");

            Integer nivelNum = rs.getObject("nivel") != null ? rs.getInt("nivel") : null;
            String estado    = rs.getString("estado");
            String informe   = rs.getString("informe");
            LocalDateTime fechaIng =
                    rs.getTimestamp("fecha_ingreso").toLocalDateTime();

            Float  temp   = rs.getObject("temperatura") != null ? rs.getFloat("temperatura") : null;
            Double fc     = rs.getObject("frec_cardiaca") != null ? rs.getDouble("frec_cardiaca") : null;
            Double fr     = rs.getObject("frec_respiratoria") != null ? rs.getDouble("frec_respiratoria") : null;
            Double sist   = rs.getObject("sistolica") != null ? rs.getDouble("sistolica") : null;
            Double diast  = rs.getObject("diastolica") != null ? rs.getDouble("diastolica") : null;

            // nombreNivel desde el enum
            String nombreNivel = null;
            if (nivelNum != null) {
                var ne = com.grupo1.ingsw_app.domain.NivelEmergencia.fromNumero(nivelNum);
                nombreNivel = ne.getNombreEnum(); // o getNivel().getDescripcion(), según tu VO
            }

            return new IngresoDetalleDTO(
                    idIngreso,
                    cuilPac,
                    nomPac,
                    apePac,
                    obraSoc,
                    nroAfi,
                    cuilEnf,
                    nomEnf,
                    apeEnf,
                    nivelNum,
                    nombreNivel,
                    estado,
                    fechaIng,
                    informe,
                    temp,
                    fc,
                    fr,
                    sist,
                    diast
            );
        }, params.toArray());
    }

}
