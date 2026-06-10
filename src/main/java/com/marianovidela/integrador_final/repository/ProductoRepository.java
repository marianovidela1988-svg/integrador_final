package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);


    /* Forma robusta es hacer el descuento atómico en la base,
    con un UPDATE condicional, en vez de leer y después escribir.
    La condición stock >= cantidad y la resta viajan en una sola operación,
    y la base bloquea la fila,
    así que nunca dos pedidos pasan el chequeo a la vez*/
    @Modifying
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad " +
            "WHERE p.id = :id AND p.stock >= :cantidad")
    int descontarStock(@Param("id") Long id, @Param("cantidad") Integer cantidad);
}

