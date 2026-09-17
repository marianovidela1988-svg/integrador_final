package com.marianovidela.integrador_final.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private Long productoId;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precio;
}
