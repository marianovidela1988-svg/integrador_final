package com.marianovidela.integrador_final.mapper;

import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.model.Administrador;
import org.springframework.stereotype.Component;

@Component
public class AdministradorMapper {
    public AdministradorDTO toDTO(Administrador administrador) {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(administrador.getId());
        dto.setNombre(administrador.getNombre());
        dto.setApellido(administrador.getApellido());
        dto.setUser(administrador.getUser());
        dto.setPass(administrador.getPass());
        return dto;
    }

    public Administrador toEntity(AdministradorDTO dto) {
        Administrador administrador = new Administrador();
        administrador.setNombre(dto.getNombre());
        administrador.setApellido(dto.getApellido());
        administrador.setUser(dto.getUser());
        administrador.setPass(dto.getPass());
        return administrador;
    }
}
