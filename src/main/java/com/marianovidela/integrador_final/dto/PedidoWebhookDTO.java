package com.marianovidela.integrador_final.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoWebhookDTO {
    private String clienteNombre;
    private String chatId;
    private List<ItemPedidoDTO> productos;
}
