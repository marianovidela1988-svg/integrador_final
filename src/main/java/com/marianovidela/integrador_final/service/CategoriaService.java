package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.CategoriaDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.mapper.CategoriaMapper;
import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private CategoriaMapper categoriaMapper;

    // Obtener todos las Categorias
    public List<CategoriaDTO> obtenerTodos() {
        return categoriaRepository.findAll()
                .stream().map(c -> categoriaMapper.toDTO(c)).toList();
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
        categoriaRepository.delete(categoria);
        return categoriaMapper.toDTO(categoria);
    }
}
