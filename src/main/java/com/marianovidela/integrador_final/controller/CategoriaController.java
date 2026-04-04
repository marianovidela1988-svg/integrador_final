package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.CategoriaDTO;
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
    public List<CategoriaDTO> obtenerTodos() {
        return categoriaService.obtenerTodos();
    }

    // Crear Categoria
    @PostMapping
    public CategoriaDTO crear(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        return categoriaService.guardar(categoriaDTO);
    }

    // Actualizar Categoria
    @PutMapping
    public CategoriaDTO actualizar(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        return categoriaService.guardar(categoriaDTO);
    }

    // Eliminar Categoria
    @DeleteMapping("/{id}")
    public CategoriaDTO eliminar(@PathVariable Long id) {
        return categoriaService.eliminar(id);
    }

}
