package com.marianovidela.integrador_final.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.model.Administrador;
import com.marianovidela.integrador_final.repository.AdministradorRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Cubre el comportamiento de seguridad end-to-end: rutas protegidas por
 * defecto, login/logout con cookie httpOnly, y la API key separada de n8n.
 * Usa @Transactional para que el admin de prueba insertado en @BeforeEach
 * no quede persistido en la base de datos real al terminar cada test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    // Debe coincidir con el fallback de n8n.api.key en application.properties
    // cuando no hay variable de entorno N8N_API_KEY seteada.
    private static final String N8N_API_KEY_DEV_DEFAULT = "dev-only-change-me";

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testUser;
    private final String testPass = "clave-de-test-123";

    @BeforeEach
    void crearAdminDePrueba() {
        testUser = "test_admin_" + UUID.randomUUID();
        Administrador admin = new Administrador();
        admin.setUser(testUser);
        admin.setPass(passwordEncoder.encode(testPass));
        admin.setNombre("Test");
        admin.setApellido("Admin");
        administradorRepository.save(admin);
    }

    @Test
    void endpointProtegidoSinSesionDevuelve401() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void escrituraSobreProductosSinSesionDevuelve401() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lecturaDeCatalogoEsPublicaParaElBotDeTelegram() throws Exception {
        // GET /categorias y GET /productos los consume n8n directamente (sin JWT
        // de admin ni API key) para armar los menús del bot de Telegram.
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void loginConCredencialesInvalidasNoSeteaCookie() throws Exception {
        AdministradorDTO login = new AdministradorDTO(0, "", "", testUser, "clave-incorrecta");

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value("ERROR"))
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void loginValidoSeteaCookieHttpOnlyYPermiteConsultarSesion() throws Exception {
        AdministradorDTO login = new AdministradorDTO(0, "", "", testUser, testPass);

        MvcResult loginResult = mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value("OK"))
                .andReturn();

        Cookie jwtCookie = loginResult.getResponse().getCookie(JwtService.COOKIE_NAME);
        assertNotNull(jwtCookie, "Se esperaba que el login seteara la cookie " + JwtService.COOKIE_NAME);
        assertTrue(jwtCookie.isHttpOnly());

        mockMvc.perform(get("/admin/session").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value(testUser));
    }

    @Test
    void sessionSinCookieDevuelve401() throws Exception {
        mockMvc.perform(get("/admin/session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void carritoSinApiKeyDevuelve401() throws Exception {
        mockMvc.perform(get("/carrito/12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void carritoConApiKeyIncorrectaDevuelve401() throws Exception {
        mockMvc.perform(get("/carrito/12345").header("X-N8N-Api-Key", "clave-erronea"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void carritoConApiKeyCorrectaNoRequiereSesion() throws Exception {
        mockMvc.perform(get("/carrito/12345").header("X-N8N-Api-Key", N8N_API_KEY_DEV_DEFAULT))
                .andExpect(status().isOk());
    }
}
