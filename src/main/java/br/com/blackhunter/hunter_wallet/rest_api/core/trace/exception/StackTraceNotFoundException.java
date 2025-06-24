package br.com.blackhunter.hunter_wallet.rest_api.core.trace.exception;

/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * <p>Exceção lançada quando uma stack trace não é encontrada no sistema.</p>
 *
 * <p>Classe protegida - Alterações somente por CODEOWNERS.</p>
 */
public class StackTraceNotFoundException extends RuntimeException {

    /**
     * Constrói uma nova exceção com a mensagem de erro especificada.
     *
     * @param message a mensagem de erro
     */
    public StackTraceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova exceção com a mensagem de erro e a causa especificadas.
     *
     * @param message a mensagem de erro
     * @param cause a causa da exceção
     */
    public StackTraceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
