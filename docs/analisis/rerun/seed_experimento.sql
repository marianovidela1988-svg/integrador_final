-- ============================================================================
--  seed_experimento.sql
--  Catalogo y stock inicial FIJO para la re-ejecucion del experimento
--  (Anexo A). Correr en la base `proyecto_final`.
--
--  IMPORTANTE:
--   - Ejecutar ESTE script para dejar el sistema en el estado inicial conocido.
--   - Re-ejecutarlo:  (1) antes de la corrida PRETEST
--                     (2) despues de los 5 escenarios de warm-up del PRETEST
--                     (3) antes de la corrida POSTEST
--                     (4) despues de los 5 escenarios de warm-up del POSTEST
--   - NO toca las tablas `administradores` ni `usuarios`.
--   - Disenado para que SOLO los escenarios 17, 18 y 19 queden sin stock.
-- ============================================================================

USE proyecto_final;

-- --- Limpieza de las tablas del experimento (respeta el orden de las FK) ---
DELETE FROM items_pedido;
DELETE FROM pedidos;
DELETE FROM productos;
DELETE FROM categorias;
ALTER TABLE items_pedido AUTO_INCREMENT = 1;
ALTER TABLE pedidos     AUTO_INCREMENT = 1;
ALTER TABLE productos   AUTO_INCREMENT = 1;
ALTER TABLE categorias  AUTO_INCREMENT = 1;

-- --- Categorias ---
INSERT INTO categorias (id, nombre) VALUES
  (1, 'Bebidas'),
  (2, 'Cigarrillos'),
  (3, 'Golosinas'),
  (4, 'Perfumeria e Higiene'),
  (5, 'Lacteos');

-- --- Productos (id, nombre, descripcion, precio, stock, categoria_id) ---
-- El stock inicial esta calculado para que:
--   * todos los escenarios normales (1-16 y 20) se procesen con exito;
--   * los escenarios 17, 18 y 19 fallen por stock insuficiente en el momento
--     en que se ejecutan (ver escenarios_experimento.md).
INSERT INTO productos (id, nombre, descripcion, precio, stock, categoria_id) VALUES
  ( 1, 'Coca-Cola Regular',            'Gaseosa cola 500 ml',            1300, 25, 1),
  ( 2, 'Coca-Cola Zero',               'Gaseosa cola sin azucar 500 ml', 1300, 50, 1),
  ( 3, 'Pepsi Regular',                'Gaseosa cola 500 ml',            1200, 12, 1),
  ( 4, 'Sprite Regular',               'Gaseosa lima-limon 500 ml',      1200,  3, 1),
  ( 5, 'Cerveza Quilmes',              'Cerveza rubia 473 ml',           1900, 21, 1),
  ( 6, 'Fernet Branca',                'Aperitivo 750 ml',              10500, 12, 1),
  ( 7, 'Marlboro Box',                 'Cigarrillos 20 u',              2800, 30, 2),
  ( 8, 'Phillips-Morris Convertible',  'Cigarrillos 20 u',             2700, 23, 2),
  ( 9, 'Chocolate Aireado Milka',      'Tableta 100 g',                 2600, 22, 3),
  (10, 'Doritos 250gr',                'Snack de maiz 250 g',           3200, 40, 3),
  (11, 'Bombon Bon o Bon',             'Bombon de mani 15 g',            450, 45, 3),
  (12, 'Gomitas Mogul',               'Gomitas frutales 40 g',          700, 10, 3),
  (13, 'Jabon Dove',                  'Jabon de tocador 90 g',          950, 15, 4),
  (14, 'Pasta de dientes Colgate',    'Crema dental 90 g',             1500, 47, 4),
  (15, 'Queso crema La Serenisima',   'Queso untable 290 g',           2400,  2, 5),
  (16, 'Manteca La Serenisima',       'Pan de manteca 200 g',          2100,  1, 5),
  (17, 'Leche descremada LS',         'Leche descremada sachet 1 L',   1250, 28, 5);

-- --- Verificacion rapida del estado inicial ---
SELECT c.nombre AS categoria, p.id, p.nombre, p.stock
FROM productos p JOIN categorias c ON c.id = p.categoria_id
ORDER BY p.categoria_id, p.id;
