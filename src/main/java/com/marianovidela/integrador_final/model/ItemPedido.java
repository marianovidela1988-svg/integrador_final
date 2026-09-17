package com.marianovidela.integrador_final.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;
}
