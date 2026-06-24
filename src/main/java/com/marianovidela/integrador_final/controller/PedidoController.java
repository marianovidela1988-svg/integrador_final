package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.PedidoWebhookDTO;
import com.marianovidela.integrador_final.model.Pedido;
import com.marianovidela.integrador_final.service.PedidoService;
import com.marianovidela.integrador_final.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;


    @Autowired
    private RestTemplate restTemplate;

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

    // Historial paginado con filtros opcionales
    @GetMapping("/historial")
    public Map<String, Object> buscarHistorial(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(defaultValue = "") String clienteNombre,
            @RequestParam(defaultValue = "") String nombreProducto,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(required = false)  Double totalMin,
            @RequestParam(required = false)  Double totalMax,
            @RequestParam(defaultValue = "") String fecha) {

        Page<Pedido> resultado = pedidoService.buscarHistorial(
            page, size, clienteNombre, nombreProducto, estado, totalMin, totalMax, fecha);

        Map<String, Object> response = new HashMap<>();
        response.put("content",          resultado.getContent());
        response.put("totalElements",    resultado.getTotalElements());
        response.put("totalPages",       resultado.getTotalPages());
        response.put("number",           resultado.getNumber());
        response.put("numberOfElements", resultado.getNumberOfElements());
        return response;
    }

    // Admin marca el pedido como CONFIRMADO o CANCELADO
    @PutMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        Pedido pedido = pedidoService.cambiarEstado(id, estado);

        // Avisar a n8n para que notifique al cliente por Telegram
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chatId", pedido.getChatId());
            payload.put("estado", pedido.getEstado());
            payload.put("total",  pedido.getTotal());

            restTemplate.postForEntity(
                    "http://localhost:5678/webhook/estado-pedido", payload, String.class);
        } catch (Exception e) {
            // el estado ya quedó guardado; si la notificación falla, no rompemos la operación
            System.err.println("No se pudo notificar a n8n para el pedido " + id + ": " + e.getMessage());
        }

        return pedido;
    }
}
