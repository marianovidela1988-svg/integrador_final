package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.ItemPedidoDTO;
import com.marianovidela.integrador_final.dto.PedidoWebhookDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.model.Pedido;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CarritoService {

    private final Map<String, List<ItemPedidoDTO>> carritos = new ConcurrentHashMap<>();

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoService pedidoService;

    public List<ItemPedidoDTO> agregarItem(String chatId, Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        List<ItemPedidoDTO> carrito = carritos.computeIfAbsent(chatId, k -> new ArrayList<>());

        Optional<ItemPedidoDTO> existing = carrito.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setCantidad(existing.get().getCantidad() + cantidad);
        } else {
            carrito.add(new ItemPedidoDTO(productoId, producto.getNombre(), cantidad, producto.getPrecio()));
        }

        return carrito;
    }

    public List<ItemPedidoDTO> obtenerCarrito(String chatId) {
        return carritos.getOrDefault(chatId, new ArrayList<>());
    }

    public void limpiarCarrito(String chatId) {
        carritos.remove(chatId);
    }

    public Pedido confirmarPedido(String chatId, String clienteNombre) {
        List<ItemPedidoDTO> items = obtenerCarrito(chatId);
        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        PedidoWebhookDTO dto = new PedidoWebhookDTO();
        dto.setClienteNombre(clienteNombre);
        dto.setChatId(chatId);
        dto.setProductos(new ArrayList<>(items));

        Pedido pedido = pedidoService.recibirPedido(dto);
        limpiarCarrito(chatId);
        return pedido;
    }
}
