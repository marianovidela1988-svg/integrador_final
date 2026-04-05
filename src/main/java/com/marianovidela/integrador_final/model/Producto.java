package com.marianovidela.integrador_final.model;

import jakarta.persistence.*;
import lombok.*;


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
    private Double precio;
    private Integer stock;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
