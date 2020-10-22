package com.projeto.library.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Classe que inicia a aplicação
@SpringBootApplication
public class LibraryApiApplication {

	// Adicionar o ModelMapper ao contexto
	// Retorna uma instancia singleton do ModelMapper para servir toda a aplicação
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
