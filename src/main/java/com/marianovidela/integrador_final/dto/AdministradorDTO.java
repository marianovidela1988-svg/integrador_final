package com.marianovidela.integrador_final.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdministradorDTO {
    private long id;
    private String nombre;
    private String apellido;
    @NotBlank(message = "El User es obligatorio")
    private String user;
    @NotBlank(message = "El Pass es obligatorio")
    private String pass;
}
