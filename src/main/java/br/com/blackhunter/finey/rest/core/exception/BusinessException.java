/*
 * @(#)BusinessException.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

/**
 * Classe protegida - Alterações somente por CODEOWNERS.
 */

package br.com.blackhunter.finey.rest.core.exception;

import br.com.blackhunter.finey.rest.core.handler.GlobalExceptionHandler;

/**
 * Classe <code>BusinessException</code>
 * <p>Classe de exceções genéricas voltadas ao negócio.</p>
 * <p>Esta exceção é utilizada para representar erros de regras de negócio da aplicação
 * e é tratada pelo {@link GlobalExceptionHandler}
 * para retornar respostas de erro apropriadas ao cliente.</p>
 * <p>Extende: {@link RuntimeException}</p>
 * 
 * @author Black Hunter
 * @since 2025
 */
public class BusinessException extends RuntimeException {
    /**
     * Construtor da classe BusinessException.
     * 
     * @param message Mensagem de erro que descreve a exceção de negócio
     */
    public BusinessException(String message) {
        super(message);
    }
}
