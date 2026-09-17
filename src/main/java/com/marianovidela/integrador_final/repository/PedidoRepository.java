package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoOrderByFechaHoraDesc(String estado);
    List<Pedido> findAllByOrderByFechaHoraDesc();

    /* Transicion atomica a CONFIRMADO en la propia base, igual que descontarStock:
    la condicion (todavia no CONFIRMADO) y la escritura viajan en una sola operacion,
    y la base bloquea la fila, asi que dos peticiones de confirmacion casi simultaneas
    sobre el mismo pedido (doble clic, reintento de red) nunca pasan las dos el chequeo. */
    @Modifying
    @Query("UPDATE Pedido p SET p.estado = :estado WHERE p.id = :id AND p.estado <> 'CONFIRMADO'")
    int marcarComoConfirmadoSiNoLoEstaba(@Param("id") Long id, @Param("estado") String estado);

    @Query(value =
        "SELECT DISTINCT p FROM Pedido p LEFT JOIN p.items i WHERE " +
        "(:clienteNombre IS NULL OR LOWER(p.clienteNombre) LIKE LOWER(CONCAT('%', :clienteNombre, '%'))) AND " +
        "(:nombreProducto IS NULL OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombreProducto, '%'))) AND " +
        "(:estado IS NULL OR p.estado = :estado) AND " +
        "(:totalMin IS NULL OR p.total >= :totalMin) AND " +
        "(:totalMax IS NULL OR p.total <= :totalMax) AND " +
        "(:fechaDesde IS NULL OR (p.fechaHora >= :fechaDesde AND p.fechaHora < :fechaHasta)) " +
        "ORDER BY p.fechaHora DESC",
    countQuery =
        "SELECT COUNT(DISTINCT p) FROM Pedido p LEFT JOIN p.items i WHERE " +
        "(:clienteNombre IS NULL OR LOWER(p.clienteNombre) LIKE LOWER(CONCAT('%', :clienteNombre, '%'))) AND " +
        "(:nombreProducto IS NULL OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombreProducto, '%'))) AND " +
        "(:estado IS NULL OR p.estado = :estado) AND " +
        "(:totalMin IS NULL OR p.total >= :totalMin) AND " +
        "(:totalMax IS NULL OR p.total <= :totalMax) AND " +
        "(:fechaDesde IS NULL OR (p.fechaHora >= :fechaDesde AND p.fechaHora < :fechaHasta))")
    Page<Pedido> buscarHistorial(
        @Param("clienteNombre") String clienteNombre,
        @Param("nombreProducto") String nombreProducto,
        @Param("estado") String estado,
        @Param("totalMin") BigDecimal totalMin,
        @Param("totalMax") BigDecimal totalMax,
        @Param("fechaDesde") LocalDateTime fechaDesde,
        @Param("fechaHasta") LocalDateTime fechaHasta,
        Pageable pageable);
}
