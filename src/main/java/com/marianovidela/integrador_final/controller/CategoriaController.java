package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.CategoriaDTO;
import com.marianovidela.integrador_final.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    // Obtener una categoria por ID
    @GetMapping("/{id}")
    public CategoriaDTO obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id);
    }

    // Obtener todos las Categorias
    @GetMapping
    public List<CategoriaDTO> obtenerTodos() {
        return categoriaService.obtenerTodos();
    }

    // Crear Categoria
    @PostMapping
    public CategoriaDTO crear(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        return categoriaService.crear(categoriaDTO);
    }

    // Actualizar Categoria
    @PutMapping
    public CategoriaDTO actualizar(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        return categoriaService.crear(categoriaDTO);
    }

    // Eliminar Categoria
    @DeleteMapping("/{id}")
    public CategoriaDTO eliminar(@PathVariable Long id) {
        return categoriaService.eliminar(id);
    }

}
