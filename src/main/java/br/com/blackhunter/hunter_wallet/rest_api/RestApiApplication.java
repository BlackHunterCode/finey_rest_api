/*
 * @(#)RestApiApplication.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

/**
 * Classe protegida - Alterações somente por CODEOWNERS.
 */

package br.com.blackhunter.hunter_wallet.rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Classe <code>RestApiApplication</code>
 * <p>Classe principal da aplicação Hunter Wallet REST API.</p>
 * <p>Esta classe é responsável por iniciar a aplicação Spring Boot e configurar
 * o contexto da aplicação.</p>
 * 
 * @author Black Hunter
 * @since 2025
 */
@SpringBootApplication
public class RestApiApplication {

	public static void main(String[] args) {
		// Carrega as variáveis de ambiente do arquivo .env
		Dotenv dotenv = Dotenv.load();
		
		// Configura as variáveis de ambiente do sistema
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
		
		SpringApplication.run(RestApiApplication.class, args);
	}
}
