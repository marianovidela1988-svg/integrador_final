package com.marianovidela.integrador_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = "com.marianovidela.integrador_final")
public class UsuariosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuariosApiApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}