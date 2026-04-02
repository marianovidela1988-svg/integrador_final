package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtener todos las Categorias
    public List<Categoria> obtenerTodos() {
        return categoriaRepository.findAll();
    }

    // Guardar Categoria (crea o actualiza una existente)
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Eliminar Categoria
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
}
