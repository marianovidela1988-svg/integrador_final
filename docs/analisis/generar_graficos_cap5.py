# -*- coding: utf-8 -*-
"""
Genera los graficos del Capitulo 5 (Fase 5 del plan de correccion post-auditoria):
- boxplot_tpp_por_condicion.png: diagrama de cajas de TPP por condicion (pretest/postest)
- diferencias_pareadas_tpp.png: diferencias pareadas (pretest - postest) por escenario

Reutiliza la misma logica de lectura de datos que compute_stats.py, para
garantizar consistencia con los numeros ya reportados en la Tabla 5.1.

Requiere: numpy, openpyxl, matplotlib (pip install --user numpy openpyxl matplotlib)
Uso: python3 generar_graficos_cap5.py (desde docs/analisis/)
"""
import datetime
import numpy as np
import openpyxl
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

XLSX_PATH = "../Plantilla_Experimento_Pretest_Postest.xlsx"
OUT_DIR = "../diagramas"

COLOR_PRE = "#4C72B0"
COLOR_POST = "#DD8452"
COLOR_GRID = "#D9D9D9"
COLOR_TEXT = "#333333"


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


def style_axes(ax):
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)
    ax.spines["left"].set_color(COLOR_GRID)
    ax.spines["bottom"].set_color(COLOR_TEXT)
    ax.tick_params(colors=COLOR_TEXT)
    ax.yaxis.grid(True, color=COLOR_GRID, linewidth=0.8, zorder=0)
    ax.set_axisbelow(True)


def main():
    wb = openpyxl.load_workbook(XLSX_PATH, data_only=True)
    pretest = read_tpp(wb, "Pretest")
    postest = read_tpp(wb, "Postest")
    ids = list(range(1, 21))
    pre = np.array([pretest[i] for i in ids], dtype=float)
    post = np.array([postest[i] for i in ids], dtype=float)
    diffs = pre - post

    plt.rcParams["font.size"] = 11
    plt.rcParams["font.family"] = "DejaVu Sans"

    # --- Grafico 1: boxplot por condicion ---
    fig, ax = plt.subplots(figsize=(6, 4.5), dpi=200)
    style_axes(ax)
    bp = ax.boxplot(
        [pre, post],
        tick_labels=["Pretest\n(manual)", "Postest\n(sistema)"],
        widths=0.45,
        patch_artist=True,
        medianprops=dict(color="white", linewidth=2),
        whiskerprops=dict(color=COLOR_TEXT, linewidth=1.2),
        capprops=dict(color=COLOR_TEXT, linewidth=1.2),
        flierprops=dict(marker="o", markerfacecolor="none", markeredgecolor=COLOR_TEXT, markersize=6),
        zorder=3,
    )
    for patch, color in zip(bp["boxes"], [COLOR_PRE, COLOR_POST]):
        patch.set_facecolor(color)
        patch.set_edgecolor(color)
        patch.set_alpha(0.85)

    for i, data in enumerate([pre, post], start=1):
        median = np.median(data)
        ax.annotate(
            f"mediana = {median:.1f} s",
            xy=(i, median),
            xytext=(i + 0.32, median),
            fontsize=9.5,
            color=COLOR_TEXT,
            va="center",
        )

    ax.set_ylabel("TPP - Tiempo de procesamiento de pedidos (s)")
    ax.set_title("Distribución de TPP por condición (n = 20 escenarios c/u)", fontsize=12, pad=12)
    fig.tight_layout()
    fig.savefig(f"{OUT_DIR}/boxplot_tpp_por_condicion.png", facecolor="white")
    plt.close(fig)

    # --- Grafico 2: diferencias pareadas por escenario ---
    fig, ax = plt.subplots(figsize=(8, 4.5), dpi=200)
    style_axes(ax)
    colors = [COLOR_PRE if d >= 0 else COLOR_POST for d in diffs]
    ax.bar(ids, diffs, color=colors, width=0.6, zorder=3, edgecolor="none")
    ax.axhline(0, color=COLOR_TEXT, linewidth=1)

    median_diff = np.median(diffs)
    ci_low, ci_high = 8.0, 16.0  # IC95% bootstrap de la mediana de diferencias (ver salida_compute_stats.txt)
    ax.axhline(median_diff, color=COLOR_TEXT, linewidth=1.4, linestyle="--", zorder=2)
    ax.axhspan(ci_low, ci_high, color=COLOR_TEXT, alpha=0.08, zorder=1)
    ax.text(
        20.9, median_diff + 5.5,
        f"mediana de las diferencias = {median_diff:.1f} s\nIC95% bootstrap [{ci_low:.0f}; {ci_high:.0f}] s",
        fontsize=9,
        color=COLOR_TEXT,
        va="bottom",
        linespacing=1.6,
        clip_on=False,
    )

    ax.set_xticks(ids)
    ax.set_xlabel("Escenario")
    ax.set_ylabel("Diferencia pretest − postest (s)")
    ax.set_title("Diferencias pareadas de TPP por escenario (positivo = favorece al postest)", fontsize=12, pad=12)
    ax.set_xlim(0.3, 29.5)
    fig.tight_layout()
    fig.savefig(f"{OUT_DIR}/diferencias_pareadas_tpp.png", facecolor="white")
    plt.close(fig)

    print("Listo:")
    print(f"  - {OUT_DIR}/boxplot_tpp_por_condicion.png")
    print(f"  - {OUT_DIR}/diferencias_pareadas_tpp.png")
    print()
    print(f"TPP pretest:  mediana={np.median(pre):.2f}s, rango=[{pre.min():.0f};{pre.max():.0f}]s, "
          f"Q1={np.percentile(pre,25):.2f}s, Q3={np.percentile(pre,75):.2f}s")
    print(f"TPP postest:  mediana={np.median(post):.2f}s, rango=[{post.min():.0f};{post.max():.0f}]s, "
          f"Q1={np.percentile(post,25):.2f}s, Q3={np.percentile(post,75):.2f}s")


if __name__ == "__main__":
    main()
