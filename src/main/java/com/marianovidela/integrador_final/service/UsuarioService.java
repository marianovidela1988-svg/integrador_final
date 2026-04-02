package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.UsuarioDTO;
import com.marianovidela.integrador_final.model.Usuario;
import com.marianovidela.integrador_final.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los Usuarios
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream().map(usuario -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setEmail(usuario.getEmail());
            dto.setTelefono(usuario.getTelefono());
            dto.setDireccion(usuario.getDireccion());
            return dto;
        }).toList();
    }

    // Obtener por Id
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setNombre(usuario.getNombre());
                    dto.setApellido(usuario.getApellido());
                    dto.setEmail(usuario.getEmail());
                    dto.setTelefono(usuario.getTelefono());
                    dto.setDireccion(usuario.getDireccion());
                    return dto;
                });
    }

    /*
          Crear Metodo buscar por dni y por nombre
    */

    // Guardar Usario (crea o actualiza uno existente)
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Eliminar Usuario
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}