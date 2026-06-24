package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Page<Categoria> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
