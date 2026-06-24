package com.marianovidela.integrador_final.exception;

public class CategoriaConProductosException extends RuntimeException {
    private final String categoria;
    private final int cantidadProductos;

    public CategoriaConProductosException(String categoria, int cantidadProductos) {
        super("No se puede eliminar la categoría '" + categoria +
                "' porque tiene " + cantidadProductos + " producto(s) asignado(s).");
        this.categoria = categoria;
        this.cantidadProductos = cantidadProductos;
    }

    public String getCategoria() { return categoria; }
    public int getCantidadProductos() { return cantidadProductos; }
}
