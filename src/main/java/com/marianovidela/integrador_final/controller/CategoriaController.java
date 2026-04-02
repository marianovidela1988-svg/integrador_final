package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    // Obtener todos las Categorias
    @GetMapping
    public List<Categoria> obtenerTodos() {
        return categoriaService.obtenerTodos();
    }

    // Crear Categoria
    @PostMapping
    public Categoria crear(@Valid @RequestBody Categoria categoria) {
        return categoriaService.guardar(categoria);
    }

    // Actualizar Categoria
    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id,@Valid @RequestBody Categoria categoria) {
        // Se setea manualmente el id del usuario por Seguridad e Integridad
        categoria.setId(id);
        return categoriaService.guardar(categoria);
    }

    // Eliminar Categoria
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }

}
