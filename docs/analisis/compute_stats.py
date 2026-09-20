# -*- coding: utf-8 -*-
"""
Reproduce los estadisticos del Capitulo 5 (Tabla 5.1) del informe de tesis
a partir de los datos crudos del Anexo A (Plantilla_Experimento_Pretest_Postest.xlsx).

Version 2 (re-ejecucion del experimento, ver Fase 1/2 de la ruta de correccion):
- El estadistico de Wilcoxon (W+, Z) se calcula de verdad a partir de los rangos
  de las diferencias (via scipy.stats.wilcoxon), no se asume que todas las
  diferencias son positivas (hallazgo C10 de la auditoria: en los datos reales
  no todas favorecen al postest, p. ej. el escenario 8).
- Se reporta el valor p EXACTO como principal, y la aproximacion normal como
  contraste secundario (hallazgo C9).
- El tamano del efecto r = Z/sqrt(N) usa N = 2*n = observaciones totales
  (convencion de Rosenthal, 1991), y se informa tambien la variante con
  n = pares como referencia de lo que declaraba la version anterior (C8).
- Se informa la reduccion porcentual sobre MEDIAS y sobre MEDIANAS; la
  mediana es el estimador que corresponde al contraste no parametrico y al
  intervalo de confianza bootstrap (hallazgo C11).
- Version 3: se agrega al final la ESTIMACION POR INTERVALO de la reduccion
  relativa (bootstrap percentil pareado sobre los escenarios), en sus dos
  formulaciones, que son cifras distintas: (a) mediana de las diferencias
  pareadas sobre la mediana del pretest (35,9%) y (b) reduccion de las
  medianas de cada condicion (32,8%). Permite verificar si el umbral del 50%
  de H1 queda fuera del intervalo, y no solo de la cifra puntual.
- Version 4: se agrega un ANALISIS DE SENSIBILIDAD del escenario 14 del postest.
  Ese escenario se aborto (error al cargar la cantidad) y se volvio a simular; el
  intervalo registrado en el Anexo A (58 s) cubre solo la resimulacion. Las
  ejecuciones del intento abortado figuran en el log de n8n (cruzar_log_n8n.py),
  fuera del intervalo. Se recalcula el contraste con el intento abortado incluido
  para acotar cuanto depende el resultado de esa decision. No reemplaza al
  resultado principal, que usa los datos tal como se registraron.

Requiere: numpy, scipy, openpyxl (pip install numpy scipy openpyxl)
Uso: python compute_stats.py
"""
import datetime
import numpy as np
from scipy import stats
from scipy.stats import norm
import openpyxl

import cruzar_log_n8n as n8n

XLSX_PATH = "../Plantilla_Experimento_Pretest_Postest.xlsx"
BOOTSTRAP_SEED = 42
BOOTSTRAP_RESAMPLES = 10000


def to_seconds(t: datetime.time) -> int:
    return t.hour * 3600 + t.minute * 60 + t.second


def read_tpp(wb, sheet_name):
    ws = wb[sheet_name]
    tpps = {}
    for row in ws.iter_rows(min_row=2, max_row=21, values_only=True):
        scenario_id, hora_inicio, hora_fin = row[0], row[1], row[2]
        if scenario_id is None:
            continue
        tpps[int(scenario_id)] = to_seconds(hora_fin) - to_seconds(hora_inicio)
    return tpps


def main():
    wb = openpyxl.load_workbook(XLSX_PATH, data_only=True)

    pretest = read_tpp(wb, "Pretest")
    postest = read_tpp(wb, "Postest")
    assert set(pretest) == set(range(1, 21)), "Se esperaban 20 escenarios en Pretest"
    assert set(postest) == set(range(1, 21)), "Se esperaban 20 escenarios en Postest"

    ids = list(range(1, 21))
    pre = np.array([pretest[i] for i in ids], dtype=float)
    post = np.array([postest[i] for i in ids], dtype=float)
    diffs = pre - post
    n = len(diffs)
    N = 2 * n  # observaciones totales (pretest + postest), convencion de Rosenthal (1991)

    print(f"n = {n} pares (N = {N} observaciones totales)")
    print(f"TPP pretest:  media = {pre.mean():.2f} s, DE = {pre.std(ddof=1):.2f} s, "
          f"mediana = {np.median(pre):.2f} s")
    print(f"TPP postest:  media = {post.mean():.2f} s, DE = {post.std(ddof=1):.2f} s, "
          f"mediana = {np.median(post):.2f} s")

    diff_media = pre.mean() - post.mean()                # = media de las diferencias (coinciden siempre)
    mediana_de_diffs = np.median(diffs)                  # lo que testea Wilcoxon y cubre el IC bootstrap
    print(f"\nDiferencia de medias      = {diff_media:.2f} s (reduccion del {100*diff_media/pre.mean():.1f}% "
          f"sobre la media del pretest)")
    print(f"Mediana de las diferencias = {mediana_de_diffs:.2f} s (reduccion del "
          f"{100*mediana_de_diffs/np.median(pre):.1f}% sobre la mediana del pretest) "
          f"<- ESTA es la cifra que corresponde al contraste no parametrico y al IC bootstrap de abajo, "
          f"NO la resta de las medianas por separado (32.00-21.50=10.50), que es una cifra distinta "
          f"(ver hallazgo C11 de la auditoria: mediana de diferencias != diferencia de medianas).")
    print(f"Diferencias que favorecen al postest: {int((diffs > 0).sum())} de {n} "
          f"(favorecen al pretest: {int((diffs < 0).sum())}, empates: {int((diffs == 0).sum())})")

    # Shapiro-Wilk sobre las diferencias pareadas
    w_shapiro, p_shapiro = stats.shapiro(diffs)
    print(f"\nShapiro-Wilk (diferencias): W = {w_shapiro:.3f}, p = {p_shapiro:.4f}")
    usar_wilcoxon = p_shapiro < 0.05
    print(f"-> {'Se usa Wilcoxon (no parametrico)' if usar_wilcoxon else 'Se cumple normalidad: procede t de Student'}")

    # --- Wilcoxon: calculo real a partir de los rangos, no de una constante ---
    res_exact = stats.wilcoxon(pre, post, alternative="two-sided", mode="exact")
    res_approx = stats.wilcoxon(pre, post, alternative="two-sided", mode="approx",
                                 correction=True)
    print(f"\nWilcoxon, valor EXACTO (principal): W+ = {res_exact.statistic:.1f}, "
          f"p (bilateral) = {res_exact.pvalue:.3e}")
    print(f"Wilcoxon, aproximacion normal (contraste secundario): "
          f"W+ = {res_approx.statistic:.1f}, z = {res_approx.zstatistic:.4f}, "
          f"p (bilateral) = {res_approx.pvalue:.6f}")

    z = res_approx.zstatistic
    r_N = z / np.sqrt(N)      # Rosenthal (1991): N = observaciones totales -> PRINCIPAL
    r_n = z / np.sqrt(n)      # n = pares evaluados -> como referencia de la version anterior
    print(f"\nTamano del efecto r = z/sqrt(N):")
    print(f"  con N = {N} (observaciones totales, Rosenthal 1991, PRINCIPAL): r = {r_N:.4f}")
    print(f"  con n = {n} (pares, como declaraba la version anterior del informe): r = {r_n:.4f}")

    if p_shapiro >= 0.05:
        t_res = stats.ttest_rel(pre, post)
        print(f"\n(Referencia) t de Student pareada: t = {t_res.statistic:.4f}, p = {t_res.pvalue:.3e}")

    # Bootstrap percentil (semilla fija, reproducible) para la mediana de las diferencias
    boot = stats.bootstrap(
        (diffs,),
        np.median,
        n_resamples=BOOTSTRAP_RESAMPLES,
        confidence_level=0.95,
        method="percentile",
        random_state=np.random.default_rng(BOOTSTRAP_SEED),
    )
    print(f"\nBootstrap percentil ({BOOTSTRAP_RESAMPLES} remuestreos, semilla={BOOTSTRAP_SEED}) "
          f"para la mediana de las diferencias:")
    print(f"  Mediana observada = {mediana_de_diffs:.2f} s")
    print(f"  IC95% = [{boot.confidence_interval.low:.2f}; {boot.confidence_interval.high:.2f}] s")
    print(f"  (Esta es la MEDIANA DE LAS DIFERENCIAS, la cifra que debe acompanar al "
          f"{100*mediana_de_diffs/np.median(pre):.1f}% de reduccion informado como principal, "
          f"NO la resta de las medianas por separado.)")

    # --- Reduccion relativa: estimacion por intervalo (bootstrap percentil pareado) ---
    # Se remuestrean escenarios (pares pre/post juntos) con reposicion y, en cada
    # remuestreo, se recalculan las medianas. Semilla y numero de remuestreos
    # son los mismos que en el bootstrap anterior.
    rng = np.random.default_rng(BOOTSTRAP_SEED)
    idx = rng.integers(0, n, size=(BOOTSTRAP_RESAMPLES, n))
    pre_b, post_b = pre[idx], post[idx]
    med_pre_b = np.median(pre_b, axis=1)
    med_post_b = np.median(post_b, axis=1)
    red_de_difs = np.median(pre_b - post_b, axis=1) / med_pre_b   # (a)
    red_de_medianas = (med_pre_b - med_post_b) / med_pre_b        # (b)

    print(f"\nReduccion relativa, bootstrap percentil pareado "
          f"({BOOTSTRAP_RESAMPLES} remuestreos, semilla={BOOTSTRAP_SEED}):")
    for etiqueta, punto, sim in (
        ("(a) mediana de las diferencias / mediana del pretest",
         mediana_de_diffs / np.median(pre), red_de_difs),
        ("(b) reduccion de las medianas (pretest - postest) / mediana del pretest",
         (np.median(pre) - np.median(post)) / np.median(pre), red_de_medianas),
    ):
        lo, hi = np.percentile(sim, [2.5, 97.5])
        print(f"  {etiqueta}: {100*punto:.1f}%  IC95% = [{100*lo:.1f}%; {100*hi:.1f}%]  "
              f"-> {'excluye' if hi < 0.5 else 'INCLUYE'} el umbral del 50% de H1")

    # --- Sensibilidad: escenario 14 del postest con el intento abortado incluido ---
    fecha, ventanas = n8n.leer_ventanas(wb)
    assert ventanas[12][0] == 13 and ventanas[13][0] == 14
    hueco = n8n.huecos(n8n.ejecuciones_del_postest(n8n.leer_ejecuciones(), fecha, ventanas),
                       ventanas).get((13, 14), [])
    print("\n--- Sensibilidad: escenario 14 del postest (intento abortado y resimulacion) ---")
    print(f"Ventana registrada del escenario 14: {n8n.hora(ventanas[13][1])} a "
          f"{n8n.hora(ventanas[13][2])} ({int(post[13])} s), solo la resimulacion.")
    if hueco:
        inicio_abortado = min(t for t, _ in hueco)
        fin13, fin14 = ventanas[12][2], ventanas[13][2]
        print(f"Log de n8n entre los escenarios 13 y 14: {len(hueco)} ejecuciones, "
              f"{n8n.hora(inicio_abortado)} a {n8n.hora(max(t for t, _ in hueco))} "
              f"(intento abortado, fuera del intervalo registrado).")
        variantes = (
            ("registrado (solo resimulacion)", int(post[13])),
            ("con el intento abortado, desde su 1a ejecucion en n8n",
             int((fin14 - inicio_abortado).total_seconds())),
            ("cota superior: desde el fin del escenario 13",
             int((fin14 - fin13).total_seconds())),
        )
        for etiqueta, tpp14 in variantes:
            post_v = post.copy()
            post_v[13] = tpp14
            d_v = pre - post_v
            w_v = stats.wilcoxon(pre, post_v, alternative="two-sided", mode="exact")
            rng_v = np.random.default_rng(BOOTSTRAP_SEED)
            idx_v = rng_v.integers(0, n, size=(BOOTSTRAP_RESAMPLES, n))
            red_v = np.median((pre - post_v)[idx_v], axis=1) / np.median(pre[idx_v], axis=1)
            lo_v, hi_v = np.percentile(red_v, [2.5, 97.5])
            print(f"  {etiqueta}: TPP14 = {tpp14} s | media postest = {post_v.mean():.2f} s "
                  f"(reduccion de medias {100*(pre.mean()-post_v.mean())/pre.mean():.1f}%) | "
                  f"mediana de difs = {np.median(d_v):.2f} s ({100*np.median(d_v)/np.median(pre):.1f}% de la "
                  f"mediana pretest, IC95% [{100*lo_v:.1f}%; {100*hi_v:.1f}%]) | "
                  f"favorecen al pretest: {int((d_v < 0).sum())} | Wilcoxon exacto p = {w_v.pvalue:.2e}")
        print("El instante exacto de inicio del intento abortado solo puede leerse del video. "
              "Como el operador actua antes de que n8n registre su 1a ejecucion, el valor real "
              "esta entre la variante 'desde la 1a ejecucion' (piso) y la cota superior.")
    else:
        print("El log no registra ejecuciones entre los escenarios 13 y 14.")


if __name__ == "__main__":
    main()
