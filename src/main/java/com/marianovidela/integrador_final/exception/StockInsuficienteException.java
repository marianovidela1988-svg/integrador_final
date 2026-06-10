package com.marianovidela.integrador_final.exception;

public class StockInsuficienteException extends RuntimeException {
    private final String producto;
    private final int stockDisponible;

    public StockInsuficienteException(String producto, int stockDisponible) {
        super("No hay stock suficiente del producto '" + producto +
                "'. Disponible: " + stockDisponible);
        this.producto = producto;
        this.stockDisponible = stockDisponible;
    }
    public String getProducto() { return producto; }
    public int getStockDisponible() { return stockDisponible; }
}
