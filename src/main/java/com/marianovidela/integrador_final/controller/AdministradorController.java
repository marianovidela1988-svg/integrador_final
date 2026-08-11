package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.AdministradorDTO;
import com.marianovidela.integrador_final.dto.AdministradorRespuestaDTO;
import com.marianovidela.integrador_final.security.JwtService;
import com.marianovidela.integrador_final.service.AdministradorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdministradorController {
    @Autowired
    private AdministradorService administradorService;
    @Autowired
    private JwtService jwtService;

    // Obtener Administradores
    @GetMapping
    public List<AdministradorDTO> obtenerTodos() {
        return administradorService.obtenerTodos();
    }

    // Registrar un administrador
    @PostMapping("/alta")
    public AdministradorDTO registrarAdmin(@RequestBody AdministradorDTO administradorDTO){
        return administradorService.registrarAdmin(administradorDTO);
    }

    // Acceder como Administrador
    @PostMapping("/login")
    public AdministradorRespuestaDTO login(@RequestBody AdministradorDTO administradorDTO, HttpServletResponse response) {
        String user = administradorDTO.getUser();
        String pass = administradorDTO.getPass();

        AdministradorService.LoginResult resultado = administradorService.login(user, pass);

        if (resultado.isSuccessful()) {
            ResponseCookie cookie = jwtService.buildCookie(resultado.token());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return resultado.respuesta();
    }

    // Cerrar sesión
    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {
        ResponseCookie cookie = jwtService.buildLogoutCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return Map.of("respuesta", "OK", "mensaje", "Sesión cerrada.");
    }

    // Verificar sesión activa (usado por el guard del frontend)
    @GetMapping("/session")
    public Map<String, String> session(Authentication authentication) {
        return Map.of("user", authentication.getName());
    }

    // Eliminar Administrador
    @DeleteMapping("/{id}")
    public AdministradorDTO eliminar(@PathVariable Long id) {
        return administradorService.eliminar(id);
    }
}
