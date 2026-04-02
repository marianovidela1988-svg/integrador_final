package com.marianovidela.usuarios_api.controller;

import com.marianovidela.usuarios_api.dto.UsuarioDTO;
import com.marianovidela.usuarios_api.model.Usuario;
import com.marianovidela.usuarios_api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    // Obtener Usuarios por ID
    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    // Crear Usuario
    @PostMapping
    public Usuario crear(@Valid @RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    // Actualizar Usuario
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id,@Valid @RequestBody Usuario usuario) {
        // Se setea manualmente el id del usuario por Seguridad e Integridad
        usuario.setId(id);
        return usuarioService.guardar(usuario);
    }

    // Eliminar Usuario
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}