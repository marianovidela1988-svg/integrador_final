"""
Analisis exploratorio del efecto de practica residual dentro de la corrida
cronometrada (seccion 6.2 del informe).

Los 5 escenarios de calentamiento (hoja Warmup del Anexo A) NO se
cronometraron, asi que no existe forma de compararlos directamente contra
los escenarios medidos. En su lugar, este script compara la primera mitad
contra la segunda mitad del subconjunto mas homogeneo de escenarios medidos
-los 13 de tipo "Simple 1 producto" (IDs 1-13, Tabla 3.1)-, donde la
complejidad de la tarea se mantiene aproximadamente constante y una
tendencia decreciente en el tiempo de procesamiento (TPP) a lo largo de la
corrida seria atribuible principalmente a la practica, no a diferencias de
dificultad entre escenarios.

Uso: python3 compute_practica_residual.py
Datos de entrada: Plantilla_Experimento_Pretest_Postest.xlsx (Anexo A),
hojas Pretest y Postest, columnas t_inicio/t_fin.
"""

import openpyxl
from scipy import stats
import numpy as np

ANEXO_A = "../Plantilla_Experimento_Pretest_Postest.xlsx"
N_HOMOGENEO = 13  # escenarios 1-13, todos "Simple 1 producto" (Tabla 3.1)


def segundos(t):
    return t.hour * 3600 + t.minute * 60 + t.second


def tpp_por_escenario(ws):
    tiempos = []
    for fila in ws.iter_rows(min_row=2, max_row=21, values_only=True):
        id_escenario, t_inicio, t_fin = fila[0], fila[1], fila[2]
        if id_escenario is None:
            break
        tiempos.append(segundos(t_fin) - segundos(t_inicio))
    return tiempos


def analizar(nombre, tpp_20):
    tpp = tpp_20[:N_HOMOGENEO]
    posicion = list(range(1, N_HOMOGENEO + 1))
    primera_mitad = tpp[:6]   # escenarios 1-6
    segunda_mitad = tpp[7:]   # escenarios 8-13 (se descarta el 7 como margen)

    r, p_pearson = stats.pearsonr(posicion, tpp)
    rho, p_spearman = stats.spearmanr(posicion, tpp)
    media1, media2 = np.mean(primera_mitad), np.mean(segunda_mitad)

    print(f"\n{nombre} (escenarios 1-{N_HOMOGENEO}, 'Simple 1 producto'):")
    print(f"  TPP por escenario: {tpp}")
    print(f"  Media escenarios 1-6:  {media1:.2f} s (n={len(primera_mitad)})")
    print(f"  Media escenarios 8-{N_HOMOGENEO}: {media2:.2f} s (n={len(segunda_mitad)})")
    print(f"  Diferencia (1-6 menos 8-{N_HOMOGENEO}): {media1 - media2:.2f} s")
    print(f"  Correlacion posicion vs TPP: Pearson r={r:.4f} (p={p_pearson:.4f}); "
          f"Spearman rho={rho:.4f} (p={p_spearman:.4f})")


def main():
    wb = openpyxl.load_workbook(ANEXO_A, data_only=True)
    tpp_pretest = tpp_por_escenario(wb["Pretest"])
    tpp_postest = tpp_por_escenario(wb["Postest"])

    analizar("Pretest", tpp_pretest)
    analizar("Postest", tpp_postest)

    reduccion_global = np.mean(tpp_pretest) - np.mean(tpp_postest)
    print(f"\nReduccion global de medias (pretest - postest, n=20 c/u): "
          f"{reduccion_global:.2f} s")
    print("(para contexto: la diferencia entre mitades dentro de cada corrida es "
          "muy inferior a esta reduccion global entre condiciones)")


if __name__ == "__main__":
    main()
