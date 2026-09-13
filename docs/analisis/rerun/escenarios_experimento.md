# Especificación de escenarios — re-ejecución del experimento (Anexo A)

Estado inicial del catálogo: `seed_experimento.sql`.
Los escenarios se ejecutan **en orden (1 → 20)** en cada condición (pretest y postest),
sobre el **mismo** catálogo y con el **mismo** estado inicial de stock.

Se retira del diseño la dimensión "cliente nuevo vs. recurrente": el artefacto no la
representa (identifica al cliente solo por `chat_id` y `cliente_nombre`, sin relación
`usuarios`–`pedidos`), por lo que no es una variable operacionalizable. La cobertura de
variabilidad queda dada por: 1 producto vs. múltiples productos, caso normal vs. caso
límite vs. stock insuficiente.

---

## Escenarios de calentamiento (warm-up) — EXCLUIDOS del análisis

Se ejecutan 5 antes de cada condición para superar la curva de aprendizaje. **No se
cronometran y no entran en ningún cálculo.** Después de los 5 se **vuelve a correr
`seed_experimento.sql`** para dejar el stock y la tabla `pedidos` en el estado inicial
antes de los 20 escenarios de medición.

| # | Tipo | Categoría | Producto(s) | Cant. | Resultado esperado |
|---|------|-----------|-------------|-------|--------------------|
| W1 | Simple 1 producto | Bebidas | Coca-Cola Regular | 1 | Confirmado |
| W2 | Simple 1 producto | Golosinas | Bombon Bon o Bon | 2 | Confirmado |
| W3 | Múltiple | Bebidas + Cigarrillos | Pepsi Regular + Marlboro Box | 1 c/u | Confirmado |
| W4 | Límite cantidad máx. | Golosinas | Gomitas Mogul | 10 | Confirmado |
| W5 | Error stock insuficiente | Lácteos | Manteca La Serenisima | 5 | Cancelado (stock 1 < 5) |

---

## Escenarios de medición (1–20)

| ID | Tipo | Categoría | Producto(s) | Cant. | Resultado esperado |
|----|------|-----------|-------------|-------|--------------------|
| 1  | Simple 1 producto | Bebidas | Coca-Cola Regular | 1 | Confirmado |
| 2  | Simple 1 producto | Cigarrillos | Marlboro Box | 1 | Confirmado |
| 3  | Simple 1 producto | Golosinas | Chocolate Aireado Milka | 1 | Confirmado |
| 4  | Simple 1 producto | Perfumeria e Higiene | Jabon Dove | 1 | Confirmado |
| 5  | Simple 1 producto | Golosinas | Doritos 250gr | 1 | Confirmado |
| 6  | Simple 1 producto | Bebidas | Coca-Cola Zero | 1 | Confirmado |
| 7  | Simple 1 producto | Cigarrillos | Phillips-Morris Convertible | 1 | Confirmado |
| 8  | Simple 1 producto | Golosinas | Bombon Bon o Bon | 1 | Confirmado |
| 9  | Simple 1 producto | Perfumeria e Higiene | Pasta de dientes Colgate | 1 | Confirmado |
| 10 | Simple 1 producto | Bebidas | Pepsi Regular | 1 | Confirmado |
| 11 | Simple 1 producto | Bebidas | Sprite Regular | 1 | Confirmado |
| 12 | Simple 1 producto | Golosinas | Gomitas Mogul | 1 | Confirmado |
| 13 | Simple 1 producto | Bebidas | Cerveza Quilmes | 1 | Confirmado |
| 14 | Múltiple (3 productos) | Bebidas + Bebidas + Golosinas | Coca-Cola Regular + Fernet Branca + Doritos 250gr | 1 c/u | Confirmado |
| 15 | Múltiple (2 productos) | Golosinas + Perfumeria | Chocolate Aireado Milka + Pasta de dientes Colgate | 1 c/u | Confirmado |
| 16 | Múltiple (2 productos) | Golosinas + Lácteos | Gomitas Mogul + Leche descremada LS | 1 c/u | Confirmado |
| 17 | Error stock insuficiente | Lácteos | Queso crema La Serenisima | 3 | **Cancelado** (disp. 2 < 3) |
| 18 | Error stock insuficiente | Lácteos | Manteca La Serenisima | 2 | **Cancelado** (disp. 1 < 2) |
| 19 | Error stock insuficiente | Bebidas | Sprite Regular | 4 | **Cancelado** (disp. 2 < 4, tras escenario 11) |
| 20 | Límite cantidad máxima | Golosinas | Doritos 250gr | 10 | Confirmado (disp. 38 ≥ 10) |

- **Confirmados:** 17 escenarios (1–16 y 20).
- **Cancelados por stock:** 3 escenarios (17–19).
- TED se calcula sobre los 17 confirmados (los 3 cancelados no son error de carga).

---

## Reconstrucción de stock esperada (para la auditoría NAS)

`stock físico real = stock inicial − Σ(cantidades de escenarios CONFIRMADOS que tocan el producto)`.
Se audita **la totalidad de los 17 productos** (no una muestra de 5).

| ID | Producto | Stock inicial | Consumido (escenarios) | **Stock físico esperado al cierre** |
|----|----------|---------------|------------------------|-------------------------------------|
| 1  | Coca-Cola Regular | 25 | 2 (esc. 1, 14) | **23** |
| 2  | Coca-Cola Zero | 50 | 1 (esc. 6) | **49** |
| 3  | Pepsi Regular | 12 | 1 (esc. 10) | **11** |
| 4  | Sprite Regular | 3 | 1 (esc. 11; esc. 19 se cancela) | **2** |
| 5  | Cerveza Quilmes | 21 | 1 (esc. 13) | **20** |
| 6  | Fernet Branca | 12 | 1 (esc. 14) | **11** |
| 7  | Marlboro Box | 30 | 1 (esc. 2) | **29** |
| 8  | Phillips-Morris Convertible | 23 | 1 (esc. 7) | **22** |
| 9  | Chocolate Aireado Milka | 22 | 2 (esc. 3, 15) | **20** |
| 10 | Doritos 250gr | 40 | 12 (esc. 5, 14, 20) | **28** |
| 11 | Bombon Bon o Bon | 45 | 1 (esc. 8) | **44** |
| 12 | Gomitas Mogul | 10 | 2 (esc. 12, 16) | **8** |
| 13 | Jabon Dove | 15 | 1 (esc. 4) | **14** |
| 14 | Pasta de dientes Colgate | 47 | 2 (esc. 9, 15) | **45** |
| 15 | Queso crema La Serenisima | 2 | 0 (esc. 17 se cancela) | **2** |
| 16 | Manteca La Serenisima | 1 | 0 (esc. 18 se cancela) | **1** |
| 17 | Leche descremada LS | 28 | 1 (esc. 16) | **27** |

En el **postest**, `stock registrado` = valor de `productos.stock` en la base (el sistema
descuenta al confirmar el pedido). Debe coincidir con el esperado en los 17 productos si
el operador confirmó todos los pedidos correctos; cualquier diferencia se reporta.

En el **pretest**, `stock registrado` = valor que el operador anotó en el ledger manual.
Las diferencias que aparezcan son la medición real de NAS en la modalidad manual.
