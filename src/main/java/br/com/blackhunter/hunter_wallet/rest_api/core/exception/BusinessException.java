/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.core.exception;

/**
 * <p>Classe <code>BusinessException</code>.</p>
 * <p>Classe de exceções genéricas voltadas ao negócio.</p>
 * */
public class BusinessException extends RuntimeException {
    /**
     * Construtor da classe.
     * @param message
     * */
    public BusinessException(String message) {
        super(message);
    }
}
