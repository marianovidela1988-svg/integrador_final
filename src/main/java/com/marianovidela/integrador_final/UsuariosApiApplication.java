package com.marianovidela.integrador_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.marianovidela.integrador_final")
public class UsuariosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuariosApiApplication.class, args);
	}

}


			/*
			FALTA:
				crear metodo Agregar Categoria
				crear clase Admin
				crear Login de Admin
				cambiar todos los metodos de Usuario
			*/