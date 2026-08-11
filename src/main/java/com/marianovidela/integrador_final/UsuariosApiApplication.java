package com.marianovidela.integrador_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

// UserDetailsServiceAutoConfiguration se excluye porque la autenticación es 100% vía
// JWT (JwtAuthenticationFilter setea el SecurityContext directamente); sin excluirla,
// Spring Boot genera un usuario/contraseña en memoria que no se usa nunca.
@SpringBootApplication(
		scanBasePackages = "com.marianovidela.integrador_final",
		exclude = UserDetailsServiceAutoConfiguration.class)
public class UsuariosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuariosApiApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}