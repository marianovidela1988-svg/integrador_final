package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.PedidoWebhookDTO;
import com.marianovidela.integrador_final.dto.ProductoDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.mapper.ProductoMapper;
import com.marianovidela.integrador_final.model.ItemPedido;
import com.marianovidela.integrador_final.model.Pedido;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

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

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerPendientes() {
        return pedidoRepository.findByEstadoOrderByFechaHoraDesc("PENDIENTE");
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaHoraDesc();
    }

    public Pedido cambiarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }
}
