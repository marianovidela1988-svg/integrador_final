# -*- coding: utf-8 -*-
"""
Cruza el log de ejecuciones de n8n contra las ventanas de tiempo del postest
declaradas en el Anexo A (hoja Postest), para corroborar con un registro de
maquina, independiente de la grabacion de pantalla, los veinte intervalos de TPP.

Entradas:
  - ../Plantilla_Experimento_Pretest_Postest.xlsx (hoja Postest y Metadatos_corrida)
  - rerun/n8n_executions_postest.csv (columnas id, status, startedAt, stoppedAt,
    duracion_s). Las marcas estan en UTC; hora de Argentina = UTC-3 (sin horario
    de verano). El archivo reune tres tandas de ejecuciones (ver rerun/README.md);
    aqui solo se usa la de la corrida definitiva del postest, identificada por la
    fecha de la hoja Metadatos_corrida.

Uso: python3 cruzar_log_n8n.py

Alcance: la corroboracion cubre solo el postest. El pretest es manual y no genera
registro de maquina, de modo que sus tiempos descansan unicamente en la grabacion.
"""
import csv
import datetime as dt

import openpyxl

XLSX_PATH = "../Plantilla_Experimento_Pretest_Postest.xlsx"
CSV_PATH = "rerun/n8n_executions_postest.csv"
UTC_A_ARGENTINA = dt.timedelta(hours=-3)
TOLERANCIA = dt.timedelta(seconds=3)   # margen de lectura del reloj en el video


def leer_ventanas(wb):
    """Fecha del postest (Metadatos_corrida) y ventanas (id, inicio, fin) de la hoja Postest."""
    fecha = None
    for campo, valor, *_ in wb["Metadatos_corrida"].iter_rows(values_only=True):
        if campo and str(campo).startswith("fecha_postest"):
            fecha = valor.date()
    ventanas = []
    for fila in wb["Postest"].iter_rows(min_row=2, max_row=21, values_only=True):
        if fila[0] is None:
            continue
        ventanas.append((int(fila[0]),
                         dt.datetime.combine(fecha, fila[1]),
                         dt.datetime.combine(fecha, fila[2])))
    return fecha, ventanas


def leer_ejecuciones():
    """Todas las ejecuciones del CSV como (hora local Argentina, estado)."""
    with open(CSV_PATH, encoding="utf-8-sig") as f:
        return [(dt.datetime.strptime(r["startedAt"][:19], "%Y-%m-%d %H:%M:%S")
                 + UTC_A_ARGENTINA, r["status"]) for r in csv.DictReader(f)]


def ejecuciones_del_postest(todas, fecha, ventanas):
    """Subconjunto de la corrida definitiva: mismo dia y dentro de +-1 min de las ventanas."""
    margen = dt.timedelta(minutes=1)
    ini, fin = ventanas[0][1] - margen, ventanas[-1][2] + margen
    return [e for e in todas if e[0].date() == fecha and ini <= e[0] <= fin]


def en_ventana(e, ventana):
    return ventana[1] - TOLERANCIA <= e[0] <= ventana[2] + TOLERANCIA


def huecos(ejecs, ventanas):
    """Ejecuciones que caen entre dos ventanas consecutivas: {(id_prev, id_sig): [ejecuciones]}."""
    out = {}
    for prev, sig in zip(ventanas, ventanas[1:]):
        h = [e for e in ejecs if prev[2] + TOLERANCIA < e[0] < sig[1] - TOLERANCIA]
        if h:
            out[(prev[0], sig[0])] = h
    return out


def hora(t):
    return t.strftime("%H:%M:%S")


def main():
    wb = openpyxl.load_workbook(XLSX_PATH, data_only=True)
    fecha, ventanas = leer_ventanas(wb)
    todas = leer_ejecuciones()

    por_dia = {}
    for t, _ in todas:
        por_dia[t.date()] = por_dia.get(t.date(), 0) + 1
    print(f"Registros en {CSV_PATH}: {len(todas)} (hora local de Argentina, UTC-3)")
    for d in sorted(por_dia):
        marca = "  <- corrida definitiva del postest" if d == fecha else ""
        print(f"  {d.strftime('%d/%m/%Y')}: {por_dia[d]} ejecuciones{marca}")

    ejecs = ejecuciones_del_postest(todas, fecha, ventanas)
    print(f"\nCorrida definitiva ({fecha.strftime('%d/%m/%Y')}): {len(ejecs)} ejecuciones, "
          f"{hora(min(e[0] for e in ejecs))} a {hora(max(e[0] for e in ejecs))}")
    print(f"Ventana declarada en la hoja Postest: {hora(ventanas[0][1])} a {hora(ventanas[-1][2])}\n")

    print("Escenario | Ventana declarada    | TPP (s) | Ejec. en ventana | Primera  | Ultima   | Con error")
    dentro = 0
    for v in ventanas:
        e = sorted(x for x in ejecs if en_ventana(x, v))
        dentro += len(e)
        errores = sum(1 for x in e if x[1] != "success")
        pri = hora(e[0][0]) if e else "-"
        ult = hora(e[-1][0]) if e else "-"
        print(f"{v[0]:>9} | {hora(v[1])} - {hora(v[2])} | {int((v[2]-v[1]).total_seconds()):>7} | "
              f"{len(e):>16} | {pri} | {ult} | {errores:>9}")

    fuera = len(ejecs) - dentro
    print(f"\nEjecuciones dentro de alguna ventana (tolerancia de {int(TOLERANCIA.total_seconds())} s): "
          f"{dentro} de {len(ejecs)}; fuera de toda ventana: {fuera}.")
    print("Ejecuciones fuera de ventana, agrupadas por el hueco entre escenarios consecutivos:")
    hs = huecos(ejecs, ventanas)
    if not hs:
        print("  ninguna")
    for (a, b), h in hs.items():
        h = sorted(h)
        errores = sum(1 for x in h if x[1] != "success")
        print(f"  entre los escenarios {a} y {b}: {len(h)} ejecuciones, {hora(h[0][0])} a {hora(h[-1][0])}, "
              f"{errores} con error")
    print(f"  suma de los huecos: {sum(len(h) for h in hs.values())} (deben coincidir con las {fuera} de arriba)")


if __name__ == "__main__":
    main()
