package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoOrderByFechaHoraDesc(String estado);
    List<Pedido> findAllByOrderByFechaHoraDesc();

    @Query(value =
        "SELECT DISTINCT p FROM Pedido p LEFT JOIN p.items i WHERE " +
        "(:clienteNombre IS NULL OR LOWER(p.clienteNombre) LIKE LOWER(CONCAT('%', :clienteNombre, '%'))) AND " +
        "(:nombreProducto IS NULL OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombreProducto, '%'))) AND " +
        "(:estado IS NULL OR p.estado = :estado) AND " +
        "(:totalMin IS NULL OR p.total >= :totalMin) AND " +
        "(:totalMax IS NULL OR p.total <= :totalMax) AND " +
        "(:fecha IS NULL OR p.fechaHora LIKE CONCAT(:fecha, '%')) " +
        "ORDER BY p.fechaHora DESC",
    countQuery =
        "SELECT COUNT(DISTINCT p) FROM Pedido p LEFT JOIN p.items i WHERE " +
        "(:clienteNombre IS NULL OR LOWER(p.clienteNombre) LIKE LOWER(CONCAT('%', :clienteNombre, '%'))) AND " +
        "(:nombreProducto IS NULL OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombreProducto, '%'))) AND " +
        "(:estado IS NULL OR p.estado = :estado) AND " +
        "(:totalMin IS NULL OR p.total >= :totalMin) AND " +
        "(:totalMax IS NULL OR p.total <= :totalMax) AND " +
        "(:fecha IS NULL OR p.fechaHora LIKE CONCAT(:fecha, '%'))")
    Page<Pedido> buscarHistorial(
        @Param("clienteNombre") String clienteNombre,
        @Param("nombreProducto") String nombreProducto,
        @Param("estado") String estado,
        @Param("totalMin") Double totalMin,
        @Param("totalMax") Double totalMax,
        @Param("fecha") String fecha,
        Pageable pageable);
}
