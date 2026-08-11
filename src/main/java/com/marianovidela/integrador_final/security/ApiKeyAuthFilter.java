package com.marianovidela.integrador_final.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Protege los endpoints que consume n8n (bot de Telegram) con una API key
 * compartida en vez del JWT de admin, ya que esas llamadas no vienen de un
 * administrador logueado en el panel.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-N8N-Api-Key";
    private static final List<String> PROTECTED_PATTERNS = List.of("/carrito/**", "/pedidos/webhook");

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final String expectedApiKey;

    public ApiKeyAuthFilter(@Value("${n8n.api.key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!appliesTo(requestPath(request))) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(HEADER_NAME);
        if (providedApiKey == null || !providedApiKey.equals(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"respuesta\":\"ERROR\",\"mensaje\":\"API key inválida o faltante\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean appliesTo(String path) {
        return PROTECTED_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String requestPath(HttpServletRequest request) {
        // Con el DispatcherServlet mapeado a "/", getServletPath() devuelve "" y el
        // path completo queda en getRequestURI(); por eso no se puede usar getServletPath() acá.
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
    }
}
