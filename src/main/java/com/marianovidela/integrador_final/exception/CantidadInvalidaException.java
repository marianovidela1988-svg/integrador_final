package com.marianovidela.integrador_final.exception;

public class CantidadInvalidaException extends RuntimeException {
    private final Integer cantidad;
    private final int minimo;
    private final int maximo;

    public CantidadInvalidaException(Integer cantidad, int minimo, int maximo) {
        super("Cantidad inválida (" + cantidad + "). Debe ser un valor entre " + minimo + " y " + maximo + ".");
        this.cantidad = cantidad;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public int getMinimo() {
        return minimo;
    }

    public int getMaximo() {
        return maximo;
    }
}
