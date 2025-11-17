CREATE DATABASE ingsw_app;


-- ============================================================
-- 1) Tipo ENUM para EstadoIngreso (EN_PROCESO, FINALIZADO, PENDIENTE)
-- ============================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type WHERE typname = 'estado_ingreso'
    ) THEN
CREATE TYPE estado_ingreso AS ENUM ('EN_PROCESO','FINALIZADO','PENDIENTE');
END IF;
END
$$;

-- ============================================================
-- 2) Tabla OBRA_SOCIAL
--    (usada por Paciente.afiliado.obraSocial)
-- ============================================================



CREATE TABLE IF NOT EXISTS obra_social (
                                           id      UUID PRIMARY KEY,
                                           nombre  TEXT NOT NULL UNIQUE
);

-- ============================================================
-- 3) Tabla PACIENTE
--    (modelo: Paciente extiende Persona, con Domicilio y Afiliado)
-- ============================================================
CREATE TABLE IF NOT EXISTS paciente (
                                        cuil            TEXT PRIMARY KEY,
                                        nombre          TEXT NOT NULL,
                                        apellido        TEXT,
                                        email           TEXT,
                                        calle           TEXT,
                                        numero          INTEGER,
                                        localidad       TEXT,
                                        obra_social_id  UUID,
                                        numero_afiliado TEXT,
                                        CONSTRAINT fk_paciente_obra_social
                                        FOREIGN KEY (obra_social_id) REFERENCES obra_social(id)
    );



-- (Opcional) índices adicionales si después consultás mucho por obra social o localidad
-- CREATE INDEX IF NOT EXISTS idx_paciente_obra_social ON paciente(obra_social_id);
-- CREATE INDEX IF NOT EXISTS idx_paciente_localidad   ON paciente(localidad);

-- ============================================================
-- 4) Tabla INGRESO
--    (modelo: Ingreso con Paciente, Enfermera, NivelEmergencia, signos vitales)
-- ============================================================
CREATE TABLE IF NOT EXISTS ingreso (
                         id                UUID PRIMARY KEY,
                         cuil_paciente     TEXT NOT NULL,
                         cuil_enfermera    TEXT,
                         nivel_emergencia  INTEGER NOT NULL,
                         estado_ingreso    estado_ingreso NOT NULL,
                         descripcion       TEXT NOT NULL,
                         fecha_ingreso     TIMESTAMP NOT null,
                         temperatura       NUMERIC(4,1),
                         frec_cardiaca     NUMERIC(6,2),
                         frec_respiratoria NUMERIC(6,2),
                         sistolica         NUMERIC(6,2),
                         diastolica        NUMERIC(6,2)
);

-- Índice para búsquedas por estado (findByEstadoPendiente)
CREATE INDEX IF NOT EXISTS idx_ingreso_estado
    ON ingreso(estado_ingreso);

-- Índice útil para listas ordenadas por fecha
CREATE INDEX IF NOT EXISTS idx_ingreso_fecha
    ON ingreso(fecha_ingreso DESC);

CREATE TABLE IF NOT EXISTS personal (
    cuil            TEXT PRIMARY KEY,
    tipo            TEXT NOT NULL CHECK (tipo IN ('DOCTOR', 'ENFERMERA')),
    nombre          TEXT NOT NULL,
    apellido        TEXT,
    email           TEXT,
    matricula       TEXT
    );

CREATE TABLE IF NOT EXISTS usuario_personal (
    username        TEXT PRIMARY KEY,
    password_hash   TEXT NOT NULL,
    cuil_personal   TEXT NOT NULL,
    CONSTRAINT fk_usuario_personal
    FOREIGN KEY (cuil_personal) REFERENCES personal(cuil)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS sesion (
    id TEXT PRIMARY KEY,
    cuil_persona TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sesion_personal FOREIGN KEY (cuil_persona) REFERENCES personal(cuil)
    );

CREATE INDEX IF NOT EXISTS idx_sesion_expires
    ON sesion(expires_at);
CREATE UNIQUE INDEX IF NOT EXISTS idx_personal_matricula
    ON personal(matricula);

CREATE INDEX IF NOT EXISTS idx_personal_tipo
    ON personal(tipo);

CREATE INDEX IF NOT EXISTS idx_usuario_personal_cuil
    ON usuario_personal(cuil_personal);