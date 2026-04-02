package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}