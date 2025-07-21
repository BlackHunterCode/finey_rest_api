/*
 * @(#)UserAccountCreationException.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.useraccount.exception;

import br.com.blackhunter.finey.rest.core.exception.BusinessException;

/**
 * <p>Classe <code>UserAccountCreationException</code>.</p>
 * <p>Classe de exceção para o processo de criação de contas de usuários.</p>
 *
 * <p>Extends: {@link BusinessException}</p>
 * */
public class UserAccountCreationException extends BusinessException {
    /**
     * Construtor da classe.
     * @param message
     * */
    public UserAccountCreationException(String message) {
        super(message);
    }
}
