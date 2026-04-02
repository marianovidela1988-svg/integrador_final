package com.marianovidela.usuarios_api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioDTO {

    private String nombre;
    private String email;
    private boolean activo;

}