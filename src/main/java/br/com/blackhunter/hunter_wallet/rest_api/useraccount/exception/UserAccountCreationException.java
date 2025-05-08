/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.exception;

import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;

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
