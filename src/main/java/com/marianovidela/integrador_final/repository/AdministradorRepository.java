package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByUserAndPass(String user, String pass);
}
