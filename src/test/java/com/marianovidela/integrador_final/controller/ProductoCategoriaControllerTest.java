package com.marianovidela.integrador_final.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.marianovidela.integrador_final.dto.ProductoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CRUD de productos y categorías: las lecturas (GET) son públicas (las
 * consume tambien el bot de Telegram vía n8n), las escrituras requieren
 * sesión de admin.
 */
@Transactional
class ProductoCategoriaControllerTest extends AdminAuthenticatedTestBase {

    @Test
    void crearCategoriaSinSesionDevuelve401() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"x\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearYObtenerCategoria() throws Exception {
        String nombre = "Categoria Test " + UUID.randomUUID();

        MvcResult result = mockMvc.perform(post("/categorias")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("nombre", nombre))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode creada = objectMapper.readTree(result.getResponse().getContentAsString());
        long id = creada.get("id").asLong();

        mockMvc.perform(get("/categorias/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(nombre));
    }

    @Test
    void eliminarCategoriaSinSesionDevuelve401() throws Exception {
        mockMvc.perform(delete("/categorias/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void crearProductoYListarloEnSuCategoria() throws Exception {
        MvcResult catResult = mockMvc.perform(post("/categorias")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("nombre", "Cat " + UUID.randomUUID()))))
                .andReturn();
        long catId = objectMapper.readTree(catResult.getResponse().getContentAsString()).get("id").asLong();

        ProductoDTO producto = new ProductoDTO(null, "Producto Test " + UUID.randomUUID(), "desc", 100.0, 10, catId);

        mockMvc.perform(post("/productos")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10));

        mockMvc.perform(get("/productos/categoria/" + catId + "/paginados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void crearProductoSinSesionDevuelve401() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarProductoSinSesionDevuelve401() throws Exception {
        mockMvc.perform(delete("/productos/1")).andExpect(status().isUnauthorized());
    }
}
