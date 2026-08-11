package com.marianovidela.integrador_final.security;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-with-at-least-32-characters!!";

    @Test
    void unTokenValidoDecodificaAlUsernameCorrecto() {
        JwtService jwtService = new JwtService(SECRET, 60);

        String token = jwtService.generateToken("admin1");
        Optional<String> username = jwtService.validateAndGetUsername(token);

        assertEquals(Optional.of("admin1"), username);
    }

    @Test
    void unTokenExpiradoEsRechazado() {
        JwtService jwtService = new JwtService(SECRET, -1);

        String token = jwtService.generateToken("admin1");
        Optional<String> username = jwtService.validateAndGetUsername(token);

        assertTrue(username.isEmpty());
    }

    @Test
    void unTokenFirmadoConOtroSecretoEsRechazado() {
        JwtService emisor = new JwtService(SECRET, 60);
        JwtService verificador = new JwtService("otro-secreto-completamente-distinto-32ch", 60);

        String token = emisor.generateToken("admin1");
        Optional<String> username = verificador.validateAndGetUsername(token);

        assertTrue(username.isEmpty());
    }

    @Test
    void unTokenConTextoInvalidoEsRechazado() {
        JwtService jwtService = new JwtService(SECRET, 60);

        Optional<String> username = jwtService.validateAndGetUsername("esto-no-es-un-jwt");

        assertTrue(username.isEmpty());
    }
}
