package com.marianovidela.integrador_final.exception;

public class CarritoVacioException extends RuntimeException {
    private final String chatId;

    public CarritoVacioException(String chatId) {
        super("No se puede confirmar el pedido: el carrito está vacío");
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
}
