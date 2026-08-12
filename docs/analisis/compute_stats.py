# -*- coding: utf-8 -*-
"""
Reproduce los estadisticos del Capitulo 5 (Tabla 5.1) del informe de tesis
a partir de los datos crudos del Anexo A (Plantilla_Experimento_Pretest_Postest.xlsx).

Requiere: numpy, scipy, openpyxl (pip install numpy scipy openpyxl)
Uso: python compute_stats.py
"""
import datetime
import numpy as np
from scipy import stats
from scipy.stats import norm
import openpyxl

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

    print(f"n = {n}")
    print(f"TPP pretest:  media = {pre.mean():.2f} s, DE = {pre.std(ddof=1):.2f} s")
    print(f"TPP postest:  media = {post.mean():.2f} s, DE = {post.std(ddof=1):.2f} s")
    print(f"Diferencia de medias = {diffs.mean():.2f} s (reducción del {100 * diffs.mean() / pre.mean():.1f}%)")
    print(f"Mediana de las diferencias pareadas = {np.median(diffs):.2f} s")
    print(f"Todas las diferencias favorecen al postest: {(diffs > 0).all()}")

    # Shapiro-Wilk sobre las diferencias pareadas
    w_shapiro, p_shapiro = stats.shapiro(diffs)
    print(f"\nShapiro-Wilk (diferencias): W = {w_shapiro:.3f}, p = {p_shapiro:.4f}")

    # Wilcoxon con aproximacion normal (el criterio usado en la Tabla 5.1)
    w_plus = n * (n + 1) / 2  # valor maximo posible: se alcanza porque todas las diferencias son positivas
    mu_w = n * (n + 1) / 4
    sigma_w = np.sqrt(n * (n + 1) * (2 * n + 1) / 24)
    z = (w_plus - mu_w) / sigma_w
    p_normal = 2 * (1 - norm.cdf(z))
    r = z / np.sqrt(n)
    print(f"\nWilcoxon, aproximación normal: W+ = {w_plus:.0f}, Z = {z:.4f}, "
          f"p (bilateral) = {p_normal:.6f}, r = |Z|/√n = {r:.4f}")

    # Wilcoxon exacto, para contraste (Tabla 5.1 declara el uso de la aproximacion normal)
    res_exact = stats.wilcoxon(pre, post, alternative="two-sided", mode="exact")
    print(f"Wilcoxon, cálculo exacto: p (bilateral) = {res_exact.pvalue:.3e}")

    # Bootstrap percentil (semilla fija, reproducible) para la mediana de las diferencias
    boot = stats.bootstrap(
        (diffs,),
        np.median,
        n_resamples=BOOTSTRAP_RESAMPLES,
        confidence_level=0.95,
        method="percentile",
        random_state=np.random.default_rng(BOOTSTRAP_SEED),
    )
    print(f"\nBootstrap percentil ({BOOTSTRAP_RESAMPLES} remuestreos, "
          f"semilla={BOOTSTRAP_SEED}) para la mediana de las diferencias:")
    print(f"  Mediana observada = {np.median(diffs):.2f} s")
    print(f"  IC95% = [{boot.confidence_interval.low:.2f}; {boot.confidence_interval.high:.2f}] s")


if __name__ == "__main__":
    main()
