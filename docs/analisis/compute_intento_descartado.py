# -*- coding: utf-8 -*-
"""
Compara el TPP de la pasada de postest DESCARTADA (11/09/2026) con el pretest y con el
postest definitivo (12/09/2026). Respalda la Tabla 6.1 y el punto sobre arrastre de la
seccion 6.2 del informe.

Uso (desde docs/analisis/):  python compute_intento_descartado.py
Requiere: numpy, scipy, openpyxl
"""
import datetime
import numpy as np
import openpyxl
from scipy import stats

XLSX_PRINCIPAL = "../Plantilla_Experimento_Pretest_Postest.xlsx"
XLSX_INTENTO = "rerun/intento1_postest_bug_confirmacion_doble/Anexo_A_intento1_con_postest_afectado.xlsx"


def to_seconds(t: datetime.time) -> int:
    return t.hour * 3600 + t.minute * 60 + t.second


def leer_tpp(ruta, hoja):
    ws = openpyxl.load_workbook(ruta, data_only=True)[hoja]
    tpp = {}
    for fila in ws.iter_rows(min_row=2, max_row=21, values_only=True):
        if fila[0] is None or fila[1] is None or fila[2] is None:
            continue
        tpp[int(fila[0])] = to_seconds(fila[2]) - to_seconds(fila[1])
    return tpp


pre = leer_tpp(XLSX_PRINCIPAL, "Pretest")
post = leer_tpp(XLSX_PRINCIPAL, "Postest")
intento = leer_tpp(XLSX_INTENTO, "Postest")
ids = sorted(pre)
assert ids == sorted(post) == sorted(intento) == list(range(1, 21)), "faltan escenarios"

pre = np.array([pre[i] for i in ids], dtype=float)
post = np.array([post[i] for i in ids], dtype=float)
intento = np.array([intento[i] for i in ids], dtype=float)


def wilcoxon_exacto(x, y):
    """Wilcoxon pareado bilateral EXACTO por permutacion de signos, con rangos promedio.

    Es el valor correcto cuando hay empates o diferencias nulas (scipy method='exact'
    supone que no las hay). Descarta las diferencias nulas, como es convencional.
    """
    d = x - y
    d = d[d != 0]
    n = len(d)
    r = stats.rankdata(np.abs(d))
    total = r.sum()
    observado = min(r[d < 0].sum(), r[d > 0].sum())
    idx = np.arange(1 << n, dtype=np.uint32)
    suma = np.zeros(1 << n)
    for i in range(n):
        suma += ((idx >> i) & 1) * r[i]
    return float((np.minimum(suma, total - suma) <= observado + 1e-9).mean())


def resumen(nombre, x):
    print(f"{nombre}: media = {x.mean():.2f} s, mediana = {np.median(x):.2f} s")


resumen("Pretest (10/09)", pre)
resumen("Postest descartado (11/09)", intento)
resumen("Postest definitivo (12/09)", post)

print("\n--- Cada postest contra el pretest (pareado por escenario) ---")
for nombre, x in (("Descartado (11/09)", intento), ("Definitivo (12/09)", post)):
    d = pre - x
    p = wilcoxon_exacto(pre, x)
    print(f"{nombre}: reduccion de medias = {100 * d.mean() / pre.mean():.1f}%, "
          f"mediana de diferencias = {np.median(d):.2f} s, "
          f"favorecen al pretest = {(d < 0).sum()}, Wilcoxon exacto p = {p:.2e}")

print("\n--- Definitivo contra descartado (mismo circuito, mismos escenarios) ---")
dif = intento - post
p = wilcoxon_exacto(intento, post)
print(f"Diferencia de medias = {dif.mean():.2f} s "
      f"({100 * dif.mean() / (pre.mean() - post.mean()):.0f}% de la reduccion global de {pre.mean() - post.mean():.2f} s), "
      f"Wilcoxon pareado exacto p = {p:.2e}")
