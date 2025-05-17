/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.service;

import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.ChallengeResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Classe <code>ChallengeService</code>.</p>
 * <p>Serviço responsável por gerenciar os desafios de autenticação.</p>
 * 
 * @since 1.0.0
 */
@Service
public class ChallengeService {
    // Armazena os desafios ativos: nonce -> expiresAt
    private final Map<UUID, Long> activeChallengues = new ConcurrentHashMap<>();
    
    // Tempo de expiração do desafio em minutos
    private static final int CHALLENGE_EXPIRATION_MINUTES = 5;

    /**
     * <p>Gera um novo desafio para autenticação.</p>
     * 
     * @return objeto ChallengeResponse contendo o nonce e timestamp de expiração
     */
    public ChallengeResponse generateChallenge() {
        // Limpa desafios expirados
        cleanExpiredChallenges();
        
        // Gera novo UUID para o nonce
        UUID nonce = UUID.randomUUID();
        
        // Calcula timestamp de expiração (agora + tempo de expiração)
        long expiresAt = Instant.now()
                .plus(CHALLENGE_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
                .toEpochMilli();
        
        // Armazena o desafio
        activeChallengues.put(nonce, expiresAt);
        
        return new ChallengeResponse(nonce, expiresAt);
    }

    /**
     * <p>Valida se um desafio é válido e não expirado.</p>
     * 
     * @param nonce UUID do desafio a ser validado
     * @return true se o desafio for válido e não expirado, false caso contrário
     */
    public boolean validateChallenge(UUID nonce) {
        Long expiresAt = activeChallengues.get(nonce);
        
        // Verifica se o desafio existe e não está expirado
        if (expiresAt != null && expiresAt > Instant.now().toEpochMilli()) {
            return true;
        }
        
        // Remove o desafio se estiver expirado
        activeChallengues.remove(nonce);
        return false;
    }

    /**
     * <p>Remove um desafio após ser utilizado com sucesso.</p>
     * 
     * @param nonce UUID do desafio a ser removido
     */
    public void removeChallenge(UUID nonce) {
        activeChallengues.remove(nonce);
    }

    /**
     * <p>Limpa desafios expirados do cache.</p>
     */
    private void cleanExpiredChallenges() {
        long now = Instant.now().toEpochMilli();
        activeChallengues.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
