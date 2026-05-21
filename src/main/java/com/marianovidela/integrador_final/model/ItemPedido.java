package com.marianovidela.integrador_final.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productoId;
    private String nombre;
    private Integer cantidad;
    private Double precio;
    private Double subtotal;
}
