package com.marianovidela.integrador_final.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private String nombre;
    private String apellido;
    private String email;
    private Long telefono;
    private String direccion;

}