package com.marianovidela.integrador_final.dto;

import lombok.*;

@Getter
@Setter
@ToString
public class UsuarioDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;

}