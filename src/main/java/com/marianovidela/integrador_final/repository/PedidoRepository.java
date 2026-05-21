package com.marianovidela.integrador_final.repository;

import com.marianovidela.integrador_final.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoOrderByFechaHoraDesc(String estado);
    List<Pedido> findAllByOrderByFechaHoraDesc();
}
