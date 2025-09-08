-- ========================================
-- TABLA: estados
-- ========================================
CREATE TABLE IF NOT EXISTS estados (
    id_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255)
);

-- ========================================
-- TABLA: tipo_prestamo
-- ========================================
CREATE TABLE IF NOT EXISTS tipo_prestamo (
    id_tipo_prestamo SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    monto_minimo NUMERIC(15,2) NOT NULL,
    monto_maximo NUMERIC(15,2) NOT NULL,
    tasa_interes NUMERIC(5,2) NOT NULL,
    validacion_automatica BOOLEAN DEFAULT FALSE
);

-- ========================================
-- TABLA: solicitud
-- ========================================
CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud SERIAL PRIMARY KEY,
    monto NUMERIC(15,2) NOT NULL,
    plazo INT NOT NULL,
    email VARCHAR(100),
    documento_identidad VARCHAR(30) NOT NULL,
    id_estado INT NOT NULL,
    id_tipo_prestamo INT NOT NULL,
    CONSTRAINT fk_estado FOREIGN KEY (id_estado) REFERENCES estados (id_estado),
    CONSTRAINT fk_tipo_prestamo FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo (id_tipo_prestamo)
);

-- ========================================
-- TABLA: rol
-- ========================================
CREATE TABLE IF NOT EXISTS rol (
    uniqueid SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- ========================================
-- TABLA: users (si no existe)
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    last_name VARCHAR(100),
    birthday TIMESTAMP,
    address VARCHAR(255),
    phone BIGINT,
    email VARCHAR(100) UNIQUE,
    salary NUMERIC(15,2),
    password VARCHAR(255)
);

-- Agregar columnas adicionales si no existen
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='identity_document'
    ) THEN
        ALTER TABLE users ADD COLUMN identity_document VARCHAR(50);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='id_rol'
    ) THEN
        ALTER TABLE users ADD COLUMN id_rol BIGINT;
    END IF;
END$$;

-- Relación users-rol (si no existe constraint)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name='fk_users_rol'
    ) THEN
        ALTER TABLE users
        ADD CONSTRAINT fk_users_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol(uniqueid)
        ON DELETE SET NULL
        ON UPDATE CASCADE;
    END IF;
END$$;

-- ========================================
-- Datos iniciales
-- ========================================

-- Estados
INSERT INTO estados (nombre, descripcion) VALUES
('Pendiente de revisión', 'Solicitud creada, esperando validación'),
('Aprobado', 'Solicitud aprobada por el sistema'),
('Rechazado', 'Solicitud rechazada')
ON CONFLICT DO NOTHING;

-- Tipos de préstamo
INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
('Préstamo Personal', 100000, 200000000, 15.50, TRUE),
('Préstamo Vehicular', 1000000, 500000000, 12.00, FALSE),
('Préstamo Hipotecario', 1000000, 5000000000, 9.50, FALSE)
ON CONFLICT DO NOTHING;

-- Roles
INSERT INTO rol (nombre, descripcion) VALUES
('ROLE_ADMIN','Administrador')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO rol (nombre, descripcion) VALUES
('ROLE_ASESOR','Asesor')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO rol (nombre, descripcion) VALUES
('ROLE_CLIENT','Cliente')
ON CONFLICT (nombre) DO NOTHING;

-- Usuario admin por defecto
INSERT INTO users (name, last_name, birthday, address, phone, email, salary, identity_document, password, id_rol)
SELECT
    'Admin', 'Pragma',
    '1985-01-01 00:00:00',
    'Calle Falsa 123',
    312345678,
    'admin@pragma.com',
    5000000.00,
    '11111111',
    '$2a$12$D427YaeB8WOmJx7tq60r3Ohxh/jTpSW5RPF4jg5lsQV71qj2gJePq',
    (SELECT uniqueid FROM rol WHERE nombre='ROLE_ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='admin@pragma.com');

-- Ajustar permisos sobre secuencia de solicitud
GRANT USAGE, SELECT, UPDATE ON SEQUENCE solicitud_id_solicitud_seq TO credi_user;
