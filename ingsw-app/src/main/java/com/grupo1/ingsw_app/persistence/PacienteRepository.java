package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PacienteRepository implements IPacienteRepository {

    private final JdbcTemplate jdbc;

    public PacienteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // --------- SQLs

    private static final String SQL_FIND_BY_CUIL = """
        SELECT
            p.cuil,
            p.nombre,
            p.apellido,
            p.email,
            p.calle,
            p.numero,
            p.localidad,
            p.obra_social_id,
            p.numero_afiliado,
            os.nombre AS obra_social_nombre
        FROM paciente p
        LEFT JOIN obra_social os ON p.obra_social_id = os.id
        WHERE p.cuil = ?
        """;

    private static final String SQL_UPSERT_OBRA_SOCIAL = """
        INSERT INTO obra_social (id, nombre)
        VALUES (?, ?)
        ON CONFLICT (id) DO UPDATE SET
            nombre = EXCLUDED.nombre
        """;

    private static final String SQL_FIND_OBRA_SOCIAL_BY_NAME = """
        SELECT id, nombre
        FROM obra_social
        WHERE nombre = ?
        """;

    private static final String SQL_UPSERT_PACIENTE = """
        INSERT INTO paciente (
            cuil, nombre, apellido, email,
            calle, numero, localidad,
            obra_social_id, numero_afiliado
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (cuil) DO UPDATE SET
            nombre          = EXCLUDED.nombre,
            apellido        = EXCLUDED.apellido,
            email           = EXCLUDED.email,
            calle           = EXCLUDED.calle,
            numero          = EXCLUDED.numero,
            localidad       = EXCLUDED.localidad,
            obra_social_id  = EXCLUDED.obra_social_id,
            numero_afiliado = EXCLUDED.numero_afiliado
        """;

    private static final String SQL_CLEAR = "TRUNCATE TABLE paciente RESTART IDENTITY CASCADE";

    // PacienteRepository.java

    // ... otros SQLs ...
    private static final String SQL_EXISTS_AFILIADO = """
    SELECT COUNT(*) 
    FROM paciente 
    WHERE obra_social_id = ? AND numero_afiliado = ?
    """;

    // --------- Mappers

    private final RowMapper<Paciente> mapper = (rs, rowNum) -> {
        String cuil = rs.getString("cuil");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String email = rs.getString("email");
        String calle = rs.getString("calle");
        Integer numero = (Integer) rs.getObject("numero");
        String localidad = rs.getString("localidad");

        UUID obraId = (UUID) rs.getObject("obra_social_id");
        String obraNombre = rs.getString("obra_social_nombre");
        String numeroAfiliado = rs.getString("numero_afiliado");

        // --- CASO 1: Paciente minimalista (BDD) ---
        // si no hay domicilio y no hay obra social → usar constructor simple
        boolean sinDomicilio = calle == null && numero == null && localidad == null;
        boolean sinObraSocial = obraId == null && numeroAfiliado == null;

        if (sinDomicilio && sinObraSocial && apellido == null && email == null) {
            return new Paciente(cuil, nombre);
        }

        // --- CASO 2: Paciente completo ---
        ObraSocial obra = null;
        if (obraId != null && obraNombre != null) {
            obra = new ObraSocial(obraId, obraNombre);
        }

        return new Paciente(
                cuil,
                nombre,
                apellido,
                email,
                calle,
                numero,
                localidad,
                obra,
                numeroAfiliado
        );
    };


    // --------- Implementación de IPacienteRepository

    @Override
    public Optional<Paciente> findByCuil(String cuil) {
        List<Paciente> lista = jdbc.query(SQL_FIND_BY_CUIL, mapper, cuil);
        if (lista.isEmpty()) return Optional.empty();
        return Optional.of(lista.get(0));
    }

    @Override
    public boolean existsByObraSocialAndNumero(UUID idObraSocial, String numeroAfiliado) {
        if (idObraSocial == null || numeroAfiliado == null) return false;

        Integer count = jdbc.queryForObject(
                SQL_EXISTS_AFILIADO,
                Integer.class,
                idObraSocial,
                numeroAfiliado
        );
        return count != null && count > 0;
    }

    @Override
    public void save(Paciente paciente) {
        // persistimos obra social si existe
        UUID obraId = null;
        String numeroAfiliado = null;

        if (paciente.getAfiliado() != null) {
            ObraSocial obra = paciente.getAfiliado().getObraSocial();
            if (obra != null) {
                obraId = obra.getId();

                if (obraId == null) {
                    obraId = UUID.randomUUID();
                }

                jdbc.update(SQL_UPSERT_OBRA_SOCIAL, obraId, obra.getNombre());
            }
            numeroAfiliado = paciente.getAfiliado().getNumeroAfiliado();
        }

        Cuil cuil = paciente.getCuil(); // de Persona
        Domicilio dom = null;
        try {
            var field = Paciente.class.getDeclaredField("domicilio");
            field.setAccessible(true);
            dom = (Domicilio) field.get(paciente);
        } catch (Exception ignored) {
            // si no querés usar reflection, podés agregar getters en Paciente
        }

        String calle = null;
        Integer numero = null;
        String localidad = null;
        if (dom != null) {
            // asumiendo getters si los agregás en Domicilio, sino lo mismo que arriba
            try {
                var fCalle = Domicilio.class.getDeclaredField("calle");
                var fNumero = Domicilio.class.getDeclaredField("numero");
                var fLocalidad = Domicilio.class.getDeclaredField("localidad");
                fCalle.setAccessible(true);
                fNumero.setAccessible(true);
                fLocalidad.setAccessible(true);
                calle = (String) fCalle.get(dom);
                numero = (Integer) fNumero.get(dom);
                localidad = (String) fLocalidad.get(dom);
            } catch (Exception ignored) {}
        }

        jdbc.update(SQL_UPSERT_PACIENTE,
                cuil.getValor(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getEmail(),
                calle,
                numero,
                localidad,
                obraId,
                numeroAfiliado
        );
    }

}




/* Repositorio en memoria para gestionar pacientes.
 * Cumple la interfaz de persistencia mínima para los tests de BDD.
 */
/*
@Repository
public class PacienteRepositoryInMemory implements IPacienteRepository {

    private final Map<String, Paciente> data = new HashMap<>();

    @Override
    public Optional<Paciente> findByCuil(String cuil) {
        return Optional.ofNullable(data.get(cuil));
    }

    @Override
    public void save(Paciente paciente) {
        data.put(paciente.getCuil().getValor(), paciente);
    }


    public void clear() {
        data.clear();
    }
}
*/