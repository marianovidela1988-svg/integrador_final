package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.ItemPedidoDTO;
import com.marianovidela.integrador_final.model.Pedido;
import com.marianovidela.integrador_final.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PostMapping("/{chatId}/item")
    public ResponseEntity<Map<String, Object>> agregarItem(
            @PathVariable String chatId,
            @RequestBody Map<String, Object> body) {

        Long productoId = Long.valueOf(body.get("productoId").toString());
        Integer cantidad = Integer.valueOf(body.get("cantidad").toString());
        List<ItemPedidoDTO> carrito = carritoService.agregarItem(chatId, productoId, cantidad);

        return ResponseEntity.ok(Map.of("items", carrito.size(), "carrito", carrito));
    }

    @GetMapping("/{chatId}")
    public List<ItemPedidoDTO> obtenerCarrito(@PathVariable String chatId) {
        return carritoService.obtenerCarrito(chatId);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Map<String, String>> limpiarCarrito(@PathVariable String chatId) {
        carritoService.limpiarCarrito(chatId);
        return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado"));
    }

    @PostMapping("/{chatId}/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarPedido(
            @PathVariable String chatId,
            @RequestBody Map<String, String> body) {

        String clienteNombre = body.getOrDefault("clienteNombre", "Cliente Telegram");
        Pedido pedido = carritoService.confirmarPedido(chatId, clienteNombre);

        return ResponseEntity.ok(Map.of(
                "id", pedido.getId(),
                "total", pedido.getTotal(),
                "estado", pedido.getEstado(),
                "mensaje", "Pedido confirmado exitosamente"
        ));

    }
}
