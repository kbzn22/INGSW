package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.*;
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
      i.id,
      i.cuil_paciente,
      i.cuil_enfermera,
      i.nivel_emergencia,
      i.estado_ingreso,
      i.descripcion,
      i.fecha_ingreso,
      i.temperatura,
      i.frec_cardiaca,
      i.frec_respiratoria,
      i.sistolica,
      i.diastolica,

      -- columnas del paciente
      p.cuil      AS paciente_cuil,
      p.nombre    AS paciente_nombre,
      p.apellido  AS paciente_apellido,
      p.email     AS paciente_email,
      p.calle     AS paciente_calle,
      p.numero    AS paciente_numero,
      p.localidad AS paciente_localidad,
      p.numero_afiliado AS paciente_numero_afiliado,
      p.obra_social_id  AS obra_social_id,
        os.nombre         AS obra_social_nombre
    FROM ingreso i
    JOIN paciente p ON p.cuil = i.cuil_paciente
    LEFT JOIN obra_social os ON os.id = p.obra_social_id
    WHERE i.id = ?
    """;

    private static final String SQL_FIND_BY_ESTADO = """
    SELECT
      i.id,
      i.cuil_paciente,
      i.cuil_enfermera,
      i.nivel_emergencia,
      i.estado_ingreso,
      i.descripcion,
      i.fecha_ingreso,
      i.temperatura,
      i.frec_cardiaca,
      i.frec_respiratoria,
      i.sistolica,
      i.diastolica,

      -- PACIENTE
      p.cuil      AS paciente_cuil,
      p.nombre    AS paciente_nombre,
      p.apellido  AS paciente_apellido,
      p.email     AS paciente_email,
      p.calle     AS paciente_calle,
      p.numero    AS paciente_numero,
      p.localidad AS paciente_localidad

    FROM ingreso i
    JOIN paciente p ON p.cuil = i.cuil_paciente
    WHERE i.estado_ingreso::text = ?
    ORDER BY i.nivel_emergencia ASC, i.fecha_ingreso ASC
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
          i.id,
          i.cuil_paciente,
          i.cuil_enfermera,
          i.nivel_emergencia,
          i.estado_ingreso,
          i.descripcion,
          i.fecha_ingreso,
          i.temperatura,
          i.frec_cardiaca,
          i.frec_respiratoria,
          i.sistolica,
          i.diastolica,

          
          p.cuil      AS paciente_cuil,
          p.nombre    AS paciente_nombre,
          p.apellido  AS paciente_apellido,
          p.email     AS paciente_email,
          p.calle     AS paciente_calle,
          p.numero    AS paciente_numero,
          p.localidad AS paciente_localidad,

         
          a.cuil_doctor,
          a.informe,
          a.fecha_atencion

        FROM ingreso i
        JOIN paciente p ON p.cuil = i.cuil_paciente
        JOIN atencion a ON a.ingreso_id = i.id
        WHERE i.estado_ingreso = 'EN_PROCESO'
        AND a.cuil_doctor = ?
        ORDER BY a.fecha_atencion DESC
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
      i.id,
      i.cuil_paciente,
      i.cuil_enfermera,
      i.nivel_emergencia,
      i.estado_ingreso,
      i.descripcion,
      i.fecha_ingreso,
      i.temperatura,
      i.frec_cardiaca,
      i.frec_respiratoria,
      i.sistolica,
      i.diastolica,

      -- PACIENTE (las mismas columnas que usa el mapper)
      p.cuil      AS paciente_cuil,
      p.nombre    AS paciente_nombre,
      p.apellido  AS paciente_apellido,
      p.email     AS paciente_email,
      p.calle     AS paciente_calle,
      p.numero    AS paciente_numero,
      p.localidad AS paciente_localidad

    FROM ingreso i
    JOIN paciente p ON p.cuil = i.cuil_paciente
    WHERE 1=1
    """;
    private static final String SQL_FIND_BY_NIVEL=
            "SELECT COUNT(*) FROM ingreso WHERE nivel_emergencia = ?::nivel_emergencia AND estado_ingreso = 'PENDIENTE'";



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


            Temperatura temp = rs.getBigDecimal("temperatura") != null
                    ? new Temperatura(rs.getBigDecimal("temperatura").doubleValue())
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


            UUID obraSocialId = null;
            String obraSocialNombre = null;
            String numeroAfiliado = null;

            try {
                obraSocialId = rs.getObject("obra_social_id", UUID.class);
            } catch (Exception ignored) {

            }

            try {
                obraSocialNombre = rs.getString("obra_social_nombre");
            } catch (Exception ignored) {
            }

            try {
                numeroAfiliado = rs.getString("paciente_numero_afiliado");
            } catch (Exception ignored) {
            }

            ObraSocial obraSocial = null;
            if (obraSocialId != null) {

                obraSocial = new ObraSocial(obraSocialId, obraSocialNombre);
            }

            Paciente paciente = new Paciente(
                    rs.getString("paciente_cuil"),
                    rs.getString("paciente_nombre"),
                    rs.getString("paciente_apellido"),
                    rs.getString("paciente_email"),
                    rs.getString("paciente_calle"),
                    rs.getInt("paciente_numero"),
                    rs.getString("paciente_localidad"),
                    obraSocial,
                    numeroAfiliado
            );


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



    @Override
    public List<Ingreso> findForLog(LocalDateTime desde,
                                    LocalDateTime hasta,
                                    String cuilPaciente,
                                    String cuilEnfermera) {

        StringBuilder sql = new StringBuilder(SQL_FIND_LOG_BASE);
        List<Object> params = new ArrayList<>();


        if (desde != null) {
            sql.append(" AND i.fecha_ingreso >= ? ");
            params.add(Timestamp.valueOf(desde));
        }


        if (hasta != null) {
            sql.append(" AND i.fecha_ingreso <= ? ");
            params.add(Timestamp.valueOf(hasta));
        }


        boolean tieneCuilPaciente = cuilPaciente != null && !cuilPaciente.isBlank();
        boolean tieneCuilEnfermera = cuilEnfermera != null && !cuilEnfermera.isBlank();

        if (tieneCuilPaciente || tieneCuilEnfermera) {
            sql.append(" AND ( ");

            boolean first = true;

            if (tieneCuilPaciente) {
                sql.append(" i.cuil_paciente = ? ");
                params.add(cuilPaciente);
                first = false;
            }

            if (tieneCuilEnfermera) {
                if (!first) {
                    sql.append(" OR ");
                }
                sql.append(" i.cuil_enfermera = ? ");
                params.add(cuilEnfermera);
            }

            sql.append(" ) ");
        }

        sql.append(" ORDER BY i.fecha_ingreso ASC ");


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
                ? ingreso.getTemperatura().getTemperatura()
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
    public Optional<Ingreso> findById(UUID id) {
        List<Ingreso> resultados = jdbc.query(SQL_FIND_BY_ID, mapper(), id);
        return resultados.stream().findFirst();
    }

    @Override
    public List<Ingreso> findByEstado(EstadoIngreso estado) {
        return jdbc.query(SQL_FIND_BY_ESTADO, mapper(), estado.name());
    }

    @Override
    public int countByEstado(EstadoIngreso estado) {
        return jdbc.queryForObject(SQL_COUNT_BY_ESTADO, Integer.class, estado.name());
    }

    @Override
    public Optional<Ingreso> findEnAtencionActual(String cuilDoctor) {
        return jdbc.query(SQL_FIND_EN_ATENCION, mapper(), cuilDoctor)
                .stream()
                .findFirst();
    }

    @Override
    public List<Ingreso> findDetallesParaExport(
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
            sql.append(" AND REPLACE(TRIM(cuil_paciente), '-', '') = REPLACE(TRIM(?), '-', '') ");
            params.add(cuilPaciente);
        }
        if (cuilEnfermera != null && !cuilEnfermera.isBlank()) {
            sql.append(" AND REPLACE(TRIM(cuil_enfermera), '-', '') = REPLACE(TRIM(?), '-', '') ");
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

            Double  temp   = rs.getObject("temperatura") != null ? rs.getDouble("temperatura") : null;
            Double fc      = rs.getObject("frec_cardiaca") != null ? rs.getDouble("frec_cardiaca") : null;
            Double fr      = rs.getObject("frec_respiratoria") != null ? rs.getDouble("frec_respiratoria") : null;
            Double sist    = rs.getObject("sistolica") != null ? rs.getDouble("sistolica") : null;
            Double diast   = rs.getObject("diastolica") != null ? rs.getDouble("diastolica") : null;


            NivelEmergencia nivelEmergencia = null;
            if (nivelNum != null) {
                nivelEmergencia = com.grupo1.ingsw_app.domain.NivelEmergencia.fromNumero(nivelNum);
            }


            Temperatura temperatura = temp != null ? new Temperatura(temp) : null;
            FrecuenciaCardiaca frecCard = fc != null ? new FrecuenciaCardiaca(fc) : null;
            FrecuenciaRespiratoria frecResp = fr != null ? new FrecuenciaRespiratoria(fr) : null;

            TensionArterial tensionArterial = null;
            if (sist != null && diast != null) {
                tensionArterial = new TensionArterial(sist, diast);
            }


            ObraSocial obraSocial = null;
            if (obraSoc != null) {
                obraSocial = new ObraSocial(null, obraSoc);
            }

            Paciente paciente = new Paciente(
                    cuilPac,
                    nomPac,
                    apePac,
                    null,
                    null,
                    1,
                    null,
                    obraSocial,
                    nroAfi
            );


            Enfermera enfermera = null;
            if (cuilEnf != null) {
                var personaOpt = personalRepository.findByCuil(cuilEnfermera);
                if (personaOpt.isPresent() && personaOpt.get() instanceof Enfermera e) {
                    enfermera = e;
                }
            }

            Ingreso ingreso = new Ingreso(paciente, enfermera, nivelEmergencia);
            ingreso.setId(idIngreso);
            ingreso.setEstadoIngreso(EstadoIngreso.valueOf(estado));
            ingreso.setDescripcion(informe);
            ingreso.setFechaIngreso(fechaIng);
            ingreso.setTemperatura(temperatura);
            ingreso.setFrecuenciaCardiaca(frecCard);
            ingreso.setFrecuenciaRespiratoria(frecResp);
            ingreso.setTensionArterial(tensionArterial);

            return ingreso;
        }, params.toArray());
    }

}
