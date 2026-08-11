package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.dto.AdministradorRespuestaDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.mapper.AdministradorMapper;
import com.marianovidela.integrador_final.model.Administrador;
import com.marianovidela.integrador_final.repository.AdministradorRepository;
import com.marianovidela.integrador_final.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    private AdministradorMapper administradorMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    // Obtener Administradores
    public List<AdministradorDTO> obtenerTodos() {
        return administradorRepository.findAll()
                .stream().map(administrador -> administradorMapper.toDTO(administrador)).toList();
    }

    // Registrar un administrador
    public AdministradorDTO registrarAdmin(AdministradorDTO administradorDTO){
        Administrador administrador = administradorMapper.toEntity(administradorDTO);
        administrador.setPass(passwordEncoder.encode(administrador.getPass()));
        administrador = administradorRepository.save(administrador);
        return administradorMapper.toDTO(administrador);
    }

    // Acceder como Administrador
    public LoginResult login(String user, String pass){

        Optional<Administrador> admin = administradorRepository.findByUser(user);
        AdministradorRespuestaDTO response = new AdministradorRespuestaDTO();

        if (admin.isPresent() && passwordEncoder.matches(pass, admin.get().getPass())) {
            response.setRespuesta("OK");
            response.setMensaje("Ingreso Válido.");
            String token = jwtService.generateToken(admin.get().getUser());
            return new LoginResult(response, token);
        }

        response.setRespuesta("ERROR");
        response.setMensaje("Ingreso Inválido, usuario y/o clave incorrecta");
        return new LoginResult(response, null);
    }

    // Eliminar Administrador
    public AdministradorDTO eliminar(Long id) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado"));
        administradorRepository.delete(administrador);
        return administradorMapper.toDTO(administrador);
    }

    public record LoginResult(AdministradorRespuestaDTO respuesta, String token) {
        public boolean isSuccessful() {
            return token != null;
        }
    }
}
