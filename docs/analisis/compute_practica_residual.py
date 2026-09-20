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

Ademas del analisis sobre los 13 escenarios completos (resultado principal),
el script informa una variante de sensibilidad que excluye el escenario 8
en AMBAS condiciones. El criterio de exclusion es documental, no estadistico:
en el postest, el escenario 8 registra un reintento de seleccion de categoria
(Anexo A, hoja Postest, columna de correcciones), es decir, un contratiempo
operativo puntual ajeno a la dificultad del escenario. Se aplica tambien al
pretest por simetria, aunque alli no tuvo correccion. La exclusion se define
a posteriori, de modo que la variante se reporta como sensibilidad y no
reemplaza al resultado principal.

Este analisis acota el aprendizaje DENTRO de cada corrida. No estima el
arrastre ENTRE condiciones (que el postest se beneficie de haber resuelto
los mismos 20 escenarios dos dias antes), amenaza que el diseno no permite
estimar (seccion 3.9 del informe).

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


def analizar_sin_escenario(nombre, tpp_20, excluir):
    """Variante de sensibilidad: idem analizar(), excluyendo un escenario.

    Las mitades siguen siendo escenarios 1-6 y 8-13 (el 7 se descarta como
    margen); al excluir el 8 la segunda mitad queda con los escenarios 9-13.
    """
    posiciones = [i for i in range(1, N_HOMOGENEO + 1) if i != excluir]
    tpp = [tpp_20[i - 1] for i in posiciones]
    primera_mitad = [tpp_20[i - 1] for i in posiciones if i <= 6]
    segunda_mitad = [tpp_20[i - 1] for i in posiciones if i >= 8]

    r, p_pearson = stats.pearsonr(posiciones, tpp)
    rho, p_spearman = stats.spearmanr(posiciones, tpp)
    media1, media2 = np.mean(primera_mitad), np.mean(segunda_mitad)

    print(f"\n{nombre}, excluido el escenario {excluir} "
          f"({len(posiciones)} escenarios):")
    print(f"  Media escenarios 1-6:  {media1:.2f} s (n={len(primera_mitad)})")
    print(f"  Media escenarios 8-{N_HOMOGENEO} sin el {excluir}: {media2:.2f} s "
          f"(n={len(segunda_mitad)})")
    print(f"  Diferencia (primera menos segunda mitad): {media1 - media2:.2f} s")
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

    print("\n--- Sensibilidad: excluyendo el escenario 8 (ver docstring) ---")
    analizar_sin_escenario("Pretest", tpp_pretest, 8)
    analizar_sin_escenario("Postest", tpp_postest, 8)
    print("\n(Estos resultados acotan el aprendizaje intra-corrida; no estiman el "
          "arrastre entre condiciones, seccion 3.9.)")


if __name__ == "__main__":
    main()
