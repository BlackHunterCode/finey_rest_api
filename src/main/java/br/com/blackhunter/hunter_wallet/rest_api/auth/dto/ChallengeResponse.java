/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.dto;

import java.util.UUID;

/**
 * <p>Classe <code>ChallengeResponse</code>.</p>
 * <p>DTO que representa o desafio (nonce) gerado pelo servidor para o processo de autenticação.</p>
 * 
 * @since 1.0.0
 */
public class ChallengeResponse {
    private final UUID nonce;
    private final long expiresAt;

    /**
     * <p>Construtor para <code>ChallengeResponse</code>.</p>
     * 
     * @param nonce UUID único gerado para o desafio
     * @param expiresAt timestamp em milissegundos quando o desafio expira
     */
    public ChallengeResponse(UUID nonce, long expiresAt) {
        this.nonce = nonce;
        this.expiresAt = expiresAt;
    }

    /**
     * <p>Obtém o nonce do desafio.</p>
     * 
     * @return UUID único do desafio
     */
    public UUID getNonce() {
        return nonce;
    }

    /**
     * <p>Obtém o timestamp de expiração do desafio.</p>
     * 
     * @return timestamp em milissegundos quando o desafio expira
     */
    public long getExpiresAt() {
        return expiresAt;
    }
}
