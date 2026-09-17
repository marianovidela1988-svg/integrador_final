package com.marianovidela.integrador_final.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;
    private Integer stock;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
