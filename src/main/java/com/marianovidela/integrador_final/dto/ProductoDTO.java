package com.marianovidela.integrador_final.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull(message = "El precio es obligatorio")
    private Double precio;
    @NotNull(message = "El stock es obligatorio")
    private Integer stock;
    private Long categoriaId;
}
