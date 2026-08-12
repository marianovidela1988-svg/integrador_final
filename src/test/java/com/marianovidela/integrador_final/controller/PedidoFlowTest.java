package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.repository.CategoriaRepository;
import com.marianovidela.integrador_final.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cubre el flujo de carrito -> pedido -> cambio de estado que sostiene los
 * resultados de TPP/TED/NAS del Capítulo 5 (validación de stock al confirmar,
 * y descuento de stock al pasar a CONFIRMADO desde el panel de admin).
 */
@Transactional
class PedidoFlowTest extends AdminAuthenticatedTestBase {

    // Debe coincidir con el fallback de n8n.api.key en application.properties.
    private static final String N8N_API_KEY = "dev-only-change-me";

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private EntityManager entityManager;

    private Producto crearProductoConStock(int stock) {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria Test " + UUID.randomUUID());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Producto Test " + UUID.randomUUID());
        producto.setDescripcion("desc");
        producto.setPrecio(50.0);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    private void agregarItem(String chatId, Long productoId, int cantidad) throws Exception {
        mockMvc.perform(post("/carrito/" + chatId + "/item")
                        .header("X-N8N-Api-Key", N8N_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":" + cantidad + "}"))
                .andExpect(status().isOk());
    }

    @Test
    void confirmarPedidoConStockSuficienteQuedaPendiente() throws Exception {
        Producto producto = crearProductoConStock(10);
        String chatId = "chat-" + UUID.randomUUID();
        agregarItem(chatId, producto.getId(), 2);

        mockMvc.perform(post("/carrito/" + chatId + "/confirmar")
                        .header("X-N8N-Api-Key", N8N_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(100.0));
    }

    @Test
    void confirmarPedidoConStockInsuficienteQuedaCancelado() throws Exception {
        Producto producto = crearProductoConStock(1);
        String chatId = "chat-" + UUID.randomUUID();
        agregarItem(chatId, producto.getId(), 5);

        mockMvc.perform(post("/carrito/" + chatId + "/confirmar")
                        .header("X-N8N-Api-Key", N8N_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    @Test
    void confirmarEstadoConfirmadoDescuentaStock() throws Exception {
        Producto producto = crearProductoConStock(10);
        String chatId = "chat-" + UUID.randomUUID();
        agregarItem(chatId, producto.getId(), 3);

        MvcResult confirmarResult = mockMvc.perform(post("/carrito/" + chatId + "/confirmar")
                        .header("X-N8N-Api-Key", N8N_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andReturn();
        long pedidoId = objectMapper.readTree(confirmarResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/pedidos/" + pedidoId + "/estado")
                        .cookie(jwtCookie)
                        .param("estado", "CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));

        // descontarStock hace un UPDATE en bloque (JPQL) que no pasa por el
        // cache de primer nivel; hay que limpiar el contexto de persistencia
        // para que el findById siguiente refleje el valor real de la base.
        entityManager.clear();
        Producto actualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertEquals(7, actualizado.getStock());
    }

    @Test
    void cambiarEstadoSinSesionDevuelve401() throws Exception {
        mockMvc.perform(put("/pedidos/1/estado").param("estado", "CONFIRMADO"))
                .andExpect(status().isUnauthorized());
    }
}
