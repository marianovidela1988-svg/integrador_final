package com.marianovidela.integrador_final.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private Long productoId;
    private String nombre;
    private Integer cantidad;
    private Double precio;
}
