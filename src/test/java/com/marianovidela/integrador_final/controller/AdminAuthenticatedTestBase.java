package com.marianovidela.integrador_final.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.model.Administrador;
import com.marianovidela.integrador_final.repository.AdministradorRepository;
import com.marianovidela.integrador_final.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base para tests de controllers que requieren un admin logueado: crea un
 * administrador de prueba y hace login antes de cada test, dejando la cookie
 * JWT lista en {@link #jwtCookie}. Las subclases deben ser @Transactional
 * para que el admin de prueba no quede persistido en la DB real.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AdminAuthenticatedTestBase {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected AdministradorRepository administradorRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected Cookie jwtCookie;

    @BeforeEach
    void loginComoAdminDePrueba() throws Exception {
        String testUser = "test_admin_" + UUID.randomUUID();
        Administrador admin = new Administrador();
        admin.setUser(testUser);
        admin.setPass(passwordEncoder.encode("clave-test-123"));
        administradorRepository.save(admin);

        AdministradorDTO login = new AdministradorDTO(0, "", "", testUser, "clave-test-123");
        MvcResult result = mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();
        jwtCookie = result.getResponse().getCookie(JwtService.COOKIE_NAME);
    }
}
