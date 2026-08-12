-- Esquema de la base de datos (MySQL 8.0), generado a partir de las seis entidades
-- JPA descriptas en la seccion 4.3 del informe (productos, categorias, pedidos,
-- items_pedido, usuarios, administradores). Corresponde a lo que Hibernate genera
-- automaticamente (spring.jpa.hibernate.ddl-auto=update) a partir de las clases
-- anotadas en src/main/java/.../model/, con la convencion de nombres por defecto
-- (camelCase de Java -> snake_case de columna).

CREATE DATABASE IF NOT EXISTS proyecto_final
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE proyecto_final;

-- Cuentas de los operadores del comercio que acceden al panel de administracion.
-- Entidad independiente del resto del modelo (seccion 4.3).
CREATE TABLE administradores (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    user     VARCHAR(255),
    pass     VARCHAR(255),
    apellido VARCHAR(255),
    nombre   VARCHAR(255),
    PRIMARY KEY (id)
);

-- Catalogo de rubros. productos se vincula a categorias mediante categoria_id.
CREATE TABLE categorias (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE productos (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(255),
    descripcion  VARCHAR(255),
    precio       DOUBLE,
    stock        INT,
    categoria_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_productos_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias (id)
);

-- Pedido registrado por un cliente (via Telegram) o cargado manualmente por un
-- administrador. La relacion planificada usuarios -> pedidos (seccion 4.3) no
-- esta implementada: el cliente se identifica solo por chat_id y cliente_nombre.
CREATE TABLE pedidos (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    cliente_nombre VARCHAR(255),
    chat_id        VARCHAR(255),
    total          DOUBLE,
    estado         VARCHAR(255),
    fecha_hora     VARCHAR(255),
    PRIMARY KEY (id)
);

-- Detalle linea por linea de cada pedido. producto_id es una referencia sin FK
-- declarada en JPA (solo un Long), y nombre/precio se duplican deliberadamente
-- del producto al momento de la compra (patron "snapshot de linea de pedido",
-- seccion 4.3): un cambio de precio posterior no debe alterar pedidos ya
-- registrados.
CREATE TABLE items_pedido (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    producto_id BIGINT,
    nombre      VARCHAR(255),
    cantidad    INT,
    precio      DOUBLE,
    subtotal    DOUBLE,
    pedido_id   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_items_pedido_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos (id)
);

-- Pensada para representar a los clientes del comercio; sin vinculo funcional
-- con pedidos al momento de este informe (seccion 4.3).
CREATE TABLE usuarios (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(255),
    apellido  VARCHAR(255),
    email     VARCHAR(255),
    dni       VARCHAR(255),
    direccion VARCHAR(255),
    telefono  BIGINT       NOT NULL,
    PRIMARY KEY (id)
);
