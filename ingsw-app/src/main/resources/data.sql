
INSERT INTO obra_social (id, nombre) VALUES
                                         ('11111111-1111-1111-1111-111111111111', 'OSDE'),
                                         ('22222222-2222-2222-2222-222222222222', 'PAMI')
ON CONFLICT (id) DO NOTHING;


-- ============================================================
-- 6) DATOS INICIALES: PACIENTES
--    Usa afiliados válidos:
--    OSDE-100, OSDE-200, PAMI-300, PAMI-400
-- ============================================================

INSERT INTO paciente (
    cuil, nombre, apellido, email,
    calle, numero, localidad,
    obra_social_id, numero_afiliado
) VALUES
      ('20-44555000-4', 'Laura', 'Medina', 'laura.medina@example.com',
       'San Martín', 123, 'San Miguel de Tucumán',
       '11111111-1111-1111-1111-111111111111', 'OSDE-100'),

      ('20-44666000-4', 'Pablo', 'Fernández', 'pablo.fernandez@example.com',
       'Rivadavia', 456, 'San Miguel de Tucumán',
       '11111111-1111-1111-1111-111111111111', 'OSDE-200'),

      ('20-44777000-4', 'Luis', 'Gómez', 'luis.gomez@example.com',
       'Mitre', 789, 'Yerba Buena',
       '22222222-2222-2222-2222-222222222222', 'PAMI-300'),

      ('27-44888000-3', 'María', 'Suárez', 'maria.suarez@example.com',
       'Belgrano', 321, 'Yerba Buena',
       '22222222-2222-2222-2222-222222222222', 'PAMI-400'),

      -- Paciente sin obra social (ejemplo)
      ('23-44999000-7', 'Carlos', 'Ramos', 'carlos.ramos@example.com',
       'Lavalle', 654, 'San Miguel de Tucumán',
       NULL, NULL)
ON CONFLICT (cuil) DO NOTHING;


-- ============================================================
-- 7) DATOS INICIALES: PERSONAL (2 ENFERMERAS, 2 DOCTORES)
-- ============================================================

-- Ya tenías este ejemplo de enfermero:
INSERT INTO personal (cuil, tipo, nombre, apellido, email, matricula)
VALUES ('20-12547856-4', 'ENFERMERA', 'Enzo', 'Juarez', 'enzo@hospi.com', 'ABC124')
ON CONFLICT (cuil) DO NOTHING;

-- Segunda enfermera
INSERT INTO personal (cuil, tipo, nombre, apellido, email, matricula)
VALUES ('27-33555111-3', 'ENFERMERA', 'Lucía', 'Pereyra', 'lucia.pereyra@hospi.com', 'ENF567')
ON CONFLICT (cuil) DO NOTHING;

-- Doctor 1
INSERT INTO personal (cuil, tipo, nombre, apellido, email, matricula)
VALUES ('20-30111222-6', 'DOCTOR', 'Jorge', 'Alonso', 'jorge.alonso@hospi.com', 'DOC901')
ON CONFLICT (cuil) DO NOTHING;

-- Doctor 2
INSERT INTO personal (cuil, tipo, nombre, apellido, email, matricula)
VALUES ('23-32222333-9', 'DOCTOR', 'Ana', 'López', 'ana.lopez@hospi.com', 'DOC902')
ON CONFLICT (cuil) DO NOTHING;


-- ============================================================
-- 8) DATOS INICIALES: USUARIOS ASOCIADOS AL PERSONAL
--    OJO: password_hash son placeholders, ajustá luego con hashes reales.
-- ============================================================

-- Enzo Juarez (enfermero)
INSERT INTO usuario_personal (username, password_hash, cuil_personal)
VALUES ('juareze', '$2a$10$.....hash_enzo', '20-12547856-4')
ON CONFLICT (username) DO NOTHING;

-- Lucía Pereyra (enfermera)
INSERT INTO usuario_personal (username, password_hash, cuil_personal)
VALUES ('pereyral', '$2a$10$.....hash_lucia', '27-33555111-3')
ON CONFLICT (username) DO NOTHING;

-- Jorge Alonso (doctor)
INSERT INTO usuario_personal (username, password_hash, cuil_personal)
VALUES ('alonsoj', '$2a$10$.....hash_jorge', '20-30111222-6')
ON CONFLICT (username) DO NOTHING;

-- Ana López (doctora)
INSERT INTO usuario_personal (username, password_hash, cuil_personal)
VALUES ('lopeza', '$2a$10$.....hash_ana', '23-32222333-9')
ON CONFLICT (username) DO NOTHING;
-- ========================
-- INGRESOS DE PRUEBA
-- ========================
INSERT INTO ingreso (
    id, cuil_paciente, cuil_enfermera, nivel_emergencia,
    estado_ingreso, descripcion, fecha_ingreso,
    temperatura, frec_cardiaca, frec_respiratoria, sistolica, diastolica
)
VALUES
    -- PENDIENTES

    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2','20-44666000-4','20-12547856-4',5,
     'PENDIENTE','Fuerte dolor abdominal',NOW(),38.2,110,22,140,90),

    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3','27-44888000-3','20-12547856-4',1,
     'PENDIENTE','Corte leve',NOW(),36.7,75,20,118,78),

    -- UNO EN PROCESO (ya siendo atendido)
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4','20-44555000-4','20-12547856-4',4,
     'EN_PROCESO','Fractura brazo',NOW(),37.5,95,19,130,85)
ON CONFLICT DO NOTHING;


-- ========================
-- ATENCIÓN PARA EL QUE ESTÁ EN PROCESO
-- ========================
INSERT INTO atencion (id, ingreso_id, cuil_doctor, informe, fecha_atencion)
VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4',
     '20-30111222-6',
     'Atención inicial realizada',
     NOW())
ON CONFLICT DO NOTHING;