package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.CategoriaDTO;
import com.marianovidela.integrador_final.exception.CategoriaConProductosException;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.mapper.CategoriaMapper;
import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private CategoriaMapper categoriaMapper;

    // Obtener una Categoria por ID
    public CategoriaDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        return categoriaMapper.toDTO(categoria);
    }

    // Obtener todos las Categorias
    public List<CategoriaDTO> obtenerTodos() {
        return categoriaRepository.findAll()
                .stream().map(c -> categoriaMapper.toDTO(c)).toList();
    }

    // Obtener categorías paginadas (con filtro opcional por nombre)
    public Page<CategoriaDTO> obtenerPaginado(int page, int size, String nombre) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        if (nombre != null && !nombre.isBlank()) {
            return categoriaRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable)
                    .map(categoriaMapper::toDTO);
        }
        return categoriaRepository.findAll(pageable)
                .map(categoriaMapper::toDTO);
    }

    // Crear Categoria
    public CategoriaDTO crear(CategoriaDTO categoriaDTO){
        Categoria categoria = categoriaRepository.save(categoriaMapper.toEntity(categoriaDTO));
        return categoriaMapper.toDTO(categoria);
    }

    // Actualizar Categoria
    public CategoriaDTO guardar(CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepository.findById(categoriaDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
        categoria.setNombre(categoriaDTO.getNombre());
        Categoria actualizada = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(actualizada);
    }

    // Eliminar Categoria
    public CategoriaDTO eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
        if (!categoria.getProductos().isEmpty()) {
            throw new CategoriaConProductosException(categoria.getNombre(), categoria.getProductos().size());
        }
        categoriaRepository.delete(categoria);
        return categoriaMapper.toDTO(categoria);
    }
}
