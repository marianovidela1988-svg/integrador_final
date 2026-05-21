package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.PedidoWebhookDTO;
import com.marianovidela.integrador_final.model.Pedido;
import com.marianovidela.integrador_final.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // N8N llama a este endpoint cuando el cliente confirma su pedido en Telegram
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> recibirPedido(@RequestBody PedidoWebhookDTO dto) {
        Pedido pedido = pedidoService.recibirPedido(dto);
        return ResponseEntity.ok(Map.of(
                "id", pedido.getId(),
                "estado", pedido.getEstado(),
                "mensaje", "Pedido recibido correctamente"
        ));
    }

    // Frontend hace polling aquí para ver pedidos pendientes
    @GetMapping("/pendientes")
    public List<Pedido> obtenerPendientes() {
        return pedidoService.obtenerPendientes();
    }

    // Historial completo (pendientes + atendidos)
    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoService.obtenerTodos();
    }

    // Admin marca el pedido como atendido
    @PutMapping("/{id}/atender")
    public Pedido atender(@PathVariable Long id) {
        return pedidoService.atender(id);
    }
}
