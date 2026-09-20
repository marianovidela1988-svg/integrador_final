# Evidencia cruda de la re-ejecución del experimento

Esta carpeta reúne los datos crudos que respaldan el Capítulo 5 del informe. Este
archivo indica qué corrida documenta cada conjunto de datos. Todas las horas están en
hora local de Argentina (UTC−3), salvo que se indique lo contrario.

## Corridas

| Corrida | Fecha y hora | Uso en el análisis |
|---|---|---|
| **Pretest** (manual, con planilla) | 10/09/2026, 21:44:11 a 22:00:49 | Sí: 20 escenarios cronometrados |
| **Intento fallido de postest** | 11/09/2026, ~17:08 a 17:27 | No. Se conserva porque reveló el defecto de doble descuento de stock al confirmar (ver `intento1_postest_bug_confirmacion_doble/`) |
| **Postest definitivo** (automatizado) | 12/09/2026, 21:08:13 a 21:21:53 | Sí: 20 escenarios cronometrados |
| **Pasada suplementaria de NAS del postest** | 12/09/2026, posterior al postest definitivo | Sí, solo para NAS. No hay volcados de stock de esta pasada |

La base de datos se reinició por error antes de exportar el estado final del postest
definitivo. Por eso NAS del postest se auditó en la pasada suplementaria y no hay
volcados de stock de esa corrida (informe, sección 6.2).

## Archivos

| Archivo | Contenido | Corrida |
|---|---|---|
| `escenarios_experimento.md` | Especificación de los 5 escenarios de calentamiento y los 20 de medición, y reconstrucción del stock esperado (NAS) | Todas |
| `seed_experimento.sql` | Catálogo de 17 productos con su stock inicial | Todas |
| `ledger_pretest_COMPLETADO.xlsx` | Registro manual del pretest: pedidos, precios, totales y stock | Pretest |
| `stock_ANTES_pretest.csv`, `stock_DESPUES_pretest.csv` | Volcados de stock antes y después del pretest | Pretest |
| `n8n_executions_postest.csv` | Log de ejecuciones de n8n (ver la sección siguiente) | Tres tandas |
| `intento1_postest_bug_confirmacion_doble/` | Volcados de stock, pedidos, log de n8n y planilla del intento fallido | Intento fallido |
| `Anexo_A_plantilla.xlsx` | Copia idéntica de `docs/Plantilla_Experimento_Pretest_Postest.xlsx` (Anexo A) | Pretest y postest |
| `video_pretest.mov`, `video_postest.mov` | Grabaciones de pantalla con el reloj visible: instrumento de medición de TPP | Pretest y postest |

Los videos no se versionan (superan el límite de tamaño de GitHub). Se comparten por
Google Drive; el enlace figura en la hoja `Metadatos_corrida` del Anexo A.

## `n8n_executions_postest.csv`

Contiene **446 ejecuciones de tres tandas distintas**, con las marcas en **UTC**
(hora de Argentina = UTC−3):

| Tanda | Ejecuciones | Horario (Argentina) | Relación con el análisis |
|---|---|---|---|
| 30/08/2026 | 33 | 21:22 a 23:35 | Anteriores a la re-ejecución de septiembre. No se usan |
| 11/09/2026 | 260 | 17:08 a 17:26 (178) y 18:24 a 19:15 (82) | Las 178 primeras son del intento fallido; las 82 restantes son posteriores el mismo día. No se usan |
| **12/09/2026** | **153** | **21:08:15 a 21:21:51** | **Postest definitivo. Es la tanda que corrobora los tiempos** |

Las 178 ejecuciones del 11/09 del intento fallido y las 33 del 30/08 figuran también en
`intento1_postest_bug_confirmacion_doble/n8n_executions_postest.csv`.

### Corroboración de los tiempos del postest

`../cruzar_log_n8n.py` cruza la tanda del 12/09 contra las veinte ventanas de la hoja
`Postest` del Anexo A (salida versionada: `../salida_cruzar_log_n8n.txt`). El patrón es
consistente con la composición de cada escenario: 6 ejecuciones en cada escenario de un
producto, 10 en los de dos productos y 14 en el de tres. El escenario 8 tiene 8, dos más,
por el reintento de selección de categoría que la planilla registra. 138 de las 153
ejecuciones caen dentro de alguna ventana; las otras 15 están todas entre los escenarios
13 y 14 (21:15:29 a 21:16:43, una con error) y corresponden al intento abortado del
escenario 14, que quedó fuera del intervalo cronometrado (informe, sección 6.2).

**Alcance.** Esta corroboración cubre solo el postest. El pretest es manual y no genera
registro de máquina: sus veinte tiempos descansan únicamente en la grabación de pantalla.
No es una diferencia de instrumento, que es el mismo en ambas condiciones, sino de
verificabilidad.

## Cambios en el Anexo A respecto de la versión anterior

- Las fórmulas de las columnas `TPP (s)` (hojas Pretest y Postest) y `Coincide (Si/No)`
  (hoja Auditoria_Stock) ya existían, pero el archivo se había generado sin guardar sus
  resultados, de modo que cualquier lector distinto de Excel o Google Sheets las veía en
  blanco. Ahora se conservan las fórmulas y se guarda también su resultado.
- La columna `n8n execution timestamp` de la hoja Postest se completó con la primera y
  la última ejecución de la ventana de cada escenario y su cantidad.
- La columna `Fuente del tiempo` de la hoja Pretest se completó con `video_pretest.mov`.
- Se retiró la columna `pedido_id (tabla pedidos)` de la hoja Postest: no puede
  reconstruirse, porque la base se reinició antes de exportar el estado de esa corrida.
- La hoja `Metadatos_corrida` no se modificó.
