package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.UsuarioDTO;
import com.marianovidela.integrador_final.service.UsuarioService;
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
    public UsuarioDTO obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    // Crear Usuario
    @PostMapping
    public UsuarioDTO crear(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.crear(usuarioDTO);
    }

    // Actualizar Usuario
    @PutMapping
    public UsuarioDTO actualizar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.actualizarUsuario(usuarioDTO);
    }

    // Eliminar Usuario
    @DeleteMapping("/{id}")
    public UsuarioDTO eliminar(@PathVariable Long id) {
        return usuarioService.eliminar(id);
    }
}