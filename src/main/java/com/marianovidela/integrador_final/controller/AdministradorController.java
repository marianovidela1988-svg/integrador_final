package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.dto.AdministradorRespuestaDTO;
import com.marianovidela.integrador_final.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdministradorController {
    @Autowired
    private AdministradorService administradorService;

    // Obtener Administradores
    @GetMapping
    public List<AdministradorDTO> obtenerTodos() {
        return administradorService.obtenerTodos();
    }

    // Registrar un administrador
    @PostMapping("/alta")
    public AdministradorDTO registrarAdmin(@RequestBody AdministradorDTO administradorDTO){
        return administradorService.registrarAdmin(administradorDTO);
    }

    // Acceder como Administrador
    @PostMapping("/login")
    public AdministradorRespuestaDTO login(@RequestBody AdministradorDTO administradorDTO) {
        String user = administradorDTO.getUser();
        String pass = administradorDTO.getPass();
        return administradorService.login(user, pass);
    }

    // Eliminar Administrador
    @DeleteMapping("/{id}")
    public AdministradorDTO eliminar(@PathVariable Long id) {
        return administradorService.eliminar(id);
    }
}
