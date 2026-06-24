package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.PedidoWebhookDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.model.ItemPedido;
import com.marianovidela.integrador_final.model.Pedido;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoService productoService;

    @Transactional
    public Pedido recibirPedido(PedidoWebhookDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setClienteNombre(dto.getClienteNombre());
        pedido.setChatId(dto.getChatId());

        List<ItemPedido> items = dto.getProductos().stream()
                .map(itemDTO -> {
                    ItemPedido item = new ItemPedido();
                    item.setProductoId(itemDTO.getProductoId());
                    item.setNombre(itemDTO.getNombre());
                    item.setCantidad(itemDTO.getCantidad());
                    item.setPrecio(itemDTO.getPrecio());
                    item.setSubtotal(itemDTO.getPrecio() * itemDTO.getCantidad());
                    return item;
                }).toList();

        pedido.setItems(items);
        pedido.setTotal(items.stream().mapToDouble(ItemPedido::getSubtotal).sum());

        /*
            Al llegar un pedido:
            Si algún producto no tiene stock suficiente → se guarda como CANCELADO automáticamente
            Si el stock alcanza → se guarda como PENDIENTE (el default)
            Cuando el admin confirma → cambiarEstado ya descuenta el stock (ya está implementado)
            Cuando el admin cancela → solo notifica, no toca el stock (ya está implementado)
        */
        boolean stockInsuficiente = dto.getProductos().stream().anyMatch(itemDTO -> {
            Producto producto = productoService.getById(itemDTO.getProductoId());
            return producto.getStock() < itemDTO.getCantidad();
        });

        if (stockInsuficiente) {
            pedido.setEstado("CANCELADO");
        }


        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerPendientes() {
        return pedidoRepository.findByEstadoOrderByFechaHoraDesc("PENDIENTE");
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaHoraDesc();
    }

    public Page<Pedido> buscarHistorial(int page, int size,
                                        String clienteNombre, String nombreProducto,
                                        String estado, Double totalMin, Double totalMax,
                                        String fecha) {
        return pedidoRepository.buscarHistorial(
            blankToNull(clienteNombre),
            blankToNull(nombreProducto),
            blankToNull(estado),
            totalMin,
            totalMax,
            blankToNull(fecha),
            PageRequest.of(page, size)
        );
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    @Transactional
    public Pedido cambiarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        // Validar y descontar stock
        if(Objects.equals(estado, "CONFIRMADO")){
            for (ItemPedido p : pedido.getItems()){
                productoService.descontarStock(p.getProductoId(), p.getCantidad());
            }
        }
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }
}
