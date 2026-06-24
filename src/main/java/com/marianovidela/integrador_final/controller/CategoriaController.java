package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.CategoriaDTO;
import com.marianovidela.integrador_final.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Obtener categorías paginadas (con filtro opcional por nombre)
    @GetMapping("/paginadas")
    public Map<String, Object> obtenerPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String nombre) {
        Page<CategoriaDTO> resultado = categoriaService.obtenerPaginado(page, size, nombre);
        Map<String, Object> response = new HashMap<>();
        response.put("content", resultado.getContent());
        response.put("totalElements", resultado.getTotalElements());
        response.put("totalPages", resultado.getTotalPages());
        response.put("number", resultado.getNumber());
        response.put("numberOfElements", resultado.getNumberOfElements());
        return response;
    }

    // Crear Categoria
    @PostMapping
    public CategoriaDTO crear(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        return categoriaService.crear(categoriaDTO);
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
