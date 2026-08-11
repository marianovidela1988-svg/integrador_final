package com.marianovidela.integrador_final.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cubre el escenario de "carrito vacío" señalado como deuda técnica en la
 * tesis (secciones 6.2 y 7.2): antes exponía una excepción no controlada
 * (500 crudo); ahora debe devolver un 400 con JSON legible por n8n.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CarritoControllerTest {

    // Debe coincidir con el fallback de n8n.api.key en application.properties
    // cuando no hay variable de entorno N8N_API_KEY seteada.
    private static final String N8N_API_KEY_DEV_DEFAULT = "dev-only-change-me";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void confirmarPedidoConCarritoVacioDevuelve400ConJsonLegible() throws Exception {
        String chatId = "test-chat-" + UUID.randomUUID();

        mockMvc.perform(post("/carrito/" + chatId + "/confirmar")
                        .header("X-N8N-Api-Key", N8N_API_KEY_DEV_DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CARRITO_VACIO"))
                .andExpect(jsonPath("$.chatId").value(chatId));
    }

    @Test
    void agregarItemConCantidadCeroDevuelve400() throws Exception {
        agregarItemYEsperarCantidadInvalida(0);
    }

    @Test
    void agregarItemConCantidadNegativaDevuelve400() throws Exception {
        agregarItemYEsperarCantidadInvalida(-5);
    }

    @Test
    void agregarItemConCantidadMayorA10Devuelve400() throws Exception {
        agregarItemYEsperarCantidadInvalida(11);
    }

    private void agregarItemYEsperarCantidadInvalida(int cantidad) throws Exception {
        String chatId = "test-chat-" + UUID.randomUUID();

        mockMvc.perform(post("/carrito/" + chatId + "/item")
                        .header("X-N8N-Api-Key", N8N_API_KEY_DEV_DEFAULT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\": 1, \"cantidad\": " + cantidad + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CANTIDAD_INVALIDA"))
                .andExpect(jsonPath("$.minimo").value(1))
                .andExpect(jsonPath("$.maximo").value(10));
    }
}
