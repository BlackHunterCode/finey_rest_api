/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.finey.rest.auth.service;

import br.com.blackhunter.finey.rest.auth.dto.ChallengeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Classe <code>ChallengeServiceTest</code>.</p>
 * <p>Testes unitários para o serviço de gerenciamento de desafios.</p>
 * 
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    @DisplayName("Deve gerar um novo desafio com nonce e data de expiração")
    void generateChallenge_ShouldCreateNewChallengeWithNonceAndExpirationDate() {
        // Act
        ChallengeResponse challenge = challengeService.generateChallenge();

        // Assert
        assertNotNull(challenge);
        assertNotNull(challenge.getNonce());
        assertTrue(challenge.getExpiresAt() > Instant.now().toEpochMilli());
    }

    @Test
    @DisplayName("Deve validar um desafio válido")
    void validateChallenge_WithValidChallenge_ShouldReturnTrue() {
        // Arrange
        ChallengeResponse challenge = challengeService.generateChallenge();
        UUID nonce = challenge.getNonce();

        // Act
        boolean isValid = challengeService.validateChallenge(nonce);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar um desafio inexistente")
    void validateChallenge_WithNonExistentChallenge_ShouldReturnFalse() {
        // Arrange
        UUID randomNonce = UUID.randomUUID();

        // Act
        boolean isValid = challengeService.validateChallenge(randomNonce);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve remover um desafio após validação")
    void removeChallenge_ShouldRemoveExistingChallenge() {
        // Arrange
        ChallengeResponse challenge = challengeService.generateChallenge();
        UUID nonce = challenge.getNonce();
        
        // Confirma que o desafio existe
        assertTrue(challengeService.validateChallenge(nonce));

        // Act
        challengeService.removeChallenge(nonce);

        // Assert
        assertFalse(challengeService.validateChallenge(nonce));
    }
}
