package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.dto.AdministradorRespuestaDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.mapper.AdministradorMapper;
import com.marianovidela.integrador_final.model.Administrador;
import com.marianovidela.integrador_final.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    private AdministradorMapper administradorMapper;

    // Obtener Administradores
    public List<AdministradorDTO> obtenerTodos() {
        return administradorRepository.findAll()
                .stream().map(administrador -> administradorMapper.toDTO(administrador)).toList();
    }

    // Registrar un administrador
    public AdministradorDTO registrarAdmin(AdministradorDTO administradorDTO){
        Administrador administrador = administradorRepository.save(administradorMapper.toEntity(administradorDTO));
        return administradorMapper.toDTO(administrador);
    }

    // Acceder como Administrador
    public AdministradorRespuestaDTO login(String user, String pass){

        Optional<Administrador> admin = administradorRepository
                .findByUserAndPass(user, pass);
        AdministradorRespuestaDTO response = new AdministradorRespuestaDTO();

        if (admin.isPresent()) {
            response.setRespuesta("OK");
            response.setMensaje("Ingreso Válido.");
        } else {
            response.setRespuesta("ERROR");
            response.setMensaje("Ingreso Inválido, usuario y/o clave incorrecta");
        }
        return response;
    }

    // Eliminar Administrador
    public AdministradorDTO eliminar(Long id) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));
        administradorRepository.delete(administrador);
        return administradorMapper.toDTO(administrador);
    }
}
