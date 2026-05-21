package com.marianovidela.integrador_final.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El Dni es obligatorio")
    private String dni;
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    @NotNull(message = "El teléfono es obligatorio")
    private Long telefono;

}