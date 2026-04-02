package com.marianovidela.usuarios_api.service;

import com.marianovidela.usuarios_api.dto.UsuarioDTO;
import com.marianovidela.usuarios_api.model.Usuario;
import com.marianovidela.usuarios_api.repository.UsuarioRepository;
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
            dto.setEmail(usuario.getEmail());
            dto.setActivo(usuario.isActivo());
            return dto;
        }).toList();
    }

    // Obtener por Id
    public Usuario obtenerPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

    // Guardar Usario (crea o actualiza uno existente)
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Eliminar Usuario
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}