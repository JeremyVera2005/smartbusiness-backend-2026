-- =====================================================================
-- SmartBusiness IA - Esquema de base de datos (MySQL 8.0)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS smartbusiness
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smartbusiness;

-- ---------------------------------------------------------------------
-- USUARIO: cuentas de login (dueños y empleados comparten la misma tabla;
-- lo que determina el rol es la fila correspondiente en usuario_negocio)
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    fecha_creacion  DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- NEGOCIO
-- ---------------------------------------------------------------------
CREATE TABLE negocio (
    id_negocio              BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_propietario  BIGINT NOT NULL,
    nombre                  VARCHAR(100) NOT NULL,
    tipo_negocio            ENUM('BODEGA', 'FERRETERIA', 'FARMACIA') NOT NULL,
    ubicacion               VARCHAR(150),
    tamano                  ENUM('PEQUENO', 'MEDIANO', 'GRANDE'),
    horario_apertura        TIME,
    horario_cierre          TIME,
    fecha_creacion          DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_negocio_propietario
        FOREIGN KEY (id_usuario_propietario) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- USUARIO_NEGOCIO: relación N:M con rol.
-- Un usuario puede tener muchos negocios (varias filas con distinto id_negocio).
-- Un negocio NO puede tener más de un DUEÑO: se fuerza con la columna
-- generada marca_dueno + índice único (los NULL no chocan entre sí, así
-- que solo bloquea un segundo DUEÑO; no limita la cantidad de EMPLEADOs).
-- ---------------------------------------------------------------------
CREATE TABLE usuario_negocio (
    id_usuario   BIGINT NOT NULL,
    id_negocio   BIGINT NOT NULL,
    rol          ENUM('DUENO', 'EMPLEADO') NOT NULL,
    marca_dueno  TINYINT GENERATED ALWAYS AS (CASE WHEN rol = 'DUENO' THEN 1 ELSE NULL END) STORED,
    PRIMARY KEY (id_usuario, id_negocio),
    CONSTRAINT fk_un_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_un_negocio FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio),
    UNIQUE KEY uk_un_negocio_un_solo_dueno (id_negocio, marca_dueno)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- TIPO_NEGOCIO_CONFIG: config-driven UI. Define, por tipo de negocio,
-- qué campos extra de producto y qué módulos se activan en Angular.
-- ---------------------------------------------------------------------
CREATE TABLE tipo_negocio_config (
    tipo_negocio            ENUM('BODEGA', 'FERRETERIA', 'FARMACIA') PRIMARY KEY,
    campos_producto_extra   JSON,
    modulos_activos         JSON
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PRODUCTO: catálogo genérico, con atributos_extra en JSON para lo
-- específico de cada rubro (fecha de vencimiento, material, etc.)
-- ---------------------------------------------------------------------
CREATE TABLE producto (
    id_producto     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_negocio      BIGINT NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     VARCHAR(500),
    precio_venta    DECIMAL(10,2) NOT NULL,
    precio_compra   DECIMAL(10,2),
    stock_actual    INT NOT NULL DEFAULT 0,
    stock_minimo    INT DEFAULT 0,
    categoria       VARCHAR(100),
    atributos_extra JSON,
    fecha_creacion  DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_producto_negocio FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio),
    INDEX idx_producto_negocio (id_negocio)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- MOVIMIENTO_INVENTARIO: auditoría de cada entrada/salida/ajuste
-- ---------------------------------------------------------------------
CREATE TABLE movimiento_inventario (
    id_movimiento     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_negocio        BIGINT NOT NULL,
    id_producto       BIGINT NOT NULL,
    tipo_movimiento   ENUM('ENTRADA', 'SALIDA', 'AJUSTE') NOT NULL,
    cantidad          INT NOT NULL,
    stock_resultante  INT NOT NULL,
    motivo            VARCHAR(255),
    fecha_movimiento  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimiento_negocio FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio),
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    INDEX idx_movimiento_producto_fecha (id_negocio, id_producto, fecha_movimiento)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- VENTA / DETALLE_VENTA
-- ---------------------------------------------------------------------
CREATE TABLE venta (
    id_venta            BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_negocio          BIGINT NOT NULL,
    id_usuario_vendedor BIGINT,
    total               DECIMAL(10,2) NOT NULL,
    fecha_venta         DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_venta_negocio FOREIGN KEY (id_negocio) REFERENCES negocio(id_negocio),
    CONSTRAINT fk_venta_vendedor FOREIGN KEY (id_usuario_vendedor) REFERENCES usuario(id_usuario),
    INDEX idx_venta_negocio_fecha (id_negocio, fecha_venta)
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    id_detalle       BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta         BIGINT NOT NULL,
    id_producto      BIGINT NOT NULL,
    cantidad         INT NOT NULL,
    precio_unitario  DECIMAL(10,2) NOT NULL,
    subtotal         DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    INDEX idx_detalle_producto (id_producto, id_venta)
) ENGINE=InnoDB;

-- =====================================================================
-- SEED: configuración inicial por tipo de negocio (para /api/negocios/config/{tipo})
-- =====================================================================

INSERT INTO tipo_negocio_config (tipo_negocio, campos_producto_extra, modulos_activos) VALUES
('BODEGA',
 JSON_ARRAY(),
 JSON_OBJECT('proveedores', TRUE, 'vencimientos', FALSE, 'lotes', FALSE)
),
('FERRETERIA',
 JSON_ARRAY(
   JSON_OBJECT('nombre', 'material', 'tipo', 'text', 'label', 'Material'),
   JSON_OBJECT('nombre', 'unidadMedida', 'tipo', 'text', 'label', 'Unidad de medida')
 ),
 JSON_OBJECT('proveedores', TRUE, 'vencimientos', FALSE, 'lotes', FALSE)
),
('FARMACIA',
 JSON_ARRAY(
   JSON_OBJECT('nombre', 'fechaVencimiento', 'tipo', 'date', 'label', 'Fecha de vencimiento'),
   JSON_OBJECT('nombre', 'lote', 'tipo', 'text', 'label', 'Lote'),
   JSON_OBJECT('nombre', 'requiereReceta', 'tipo', 'boolean', 'label', 'Requiere receta')
 ),
 JSON_OBJECT('proveedores', TRUE, 'vencimientos', TRUE, 'lotes', TRUE)
);
