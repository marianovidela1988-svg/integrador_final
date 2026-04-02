package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
