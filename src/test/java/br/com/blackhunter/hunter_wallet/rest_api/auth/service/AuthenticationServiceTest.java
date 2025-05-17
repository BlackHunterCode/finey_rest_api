/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.service;

import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.AuthenticationRequest;
import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.ChallengeResponse;
import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>Classe <code>AuthenticationServiceTest</code>.</p>
 * <p>Testes unitários para o serviço de autenticação.</p>
 * 
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;
    
    @Mock
    private ChallengeService challengeService;
    
    @Mock
    private HmacService hmacService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Deve gerar um novo desafio")
    void getChallenge_ShouldReturnNewChallenge() {
        // Arrange
        UUID expectedNonce = UUID.randomUUID();
        long expectedExpiration = System.currentTimeMillis() + 300000; // 5 minutos
        ChallengeResponse expectedChallenge = new ChallengeResponse(expectedNonce, expectedExpiration);
        
        when(challengeService.generateChallenge()).thenReturn(expectedChallenge);

        // Act
        ChallengeResponse actualChallenge = authenticationService.getChallenge();

        // Assert
        assertEquals(expectedChallenge, actualChallenge);
        assertEquals(expectedNonce, actualChallenge.getNonce());
        assertEquals(expectedExpiration, actualChallenge.getExpiresAt());
        verify(challengeService, times(1)).generateChallenge();
    }

    @Test
    @DisplayName("Deve autenticar com resposta ao desafio válida e retornar token JWT")
    void authenticate_WithValidChallengeResponse_ShouldReturnJwtToken() {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String signature = "valid-signature";
        String deviceId = "test-device-123";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MjM5MDIyfQ.1234567890";
        
        AuthenticationRequest request = new AuthenticationRequest(nonce, signature, deviceId);
        
        when(challengeService.validateChallenge(nonce)).thenReturn(true);
        when(hmacService.validateSignature(nonce, signature, deviceId)).thenReturn(true);
        when(jwtService.generateToken(any(Authentication.class))).thenReturn(expectedToken);

        // Act
        String actualToken = authenticationService.authenticate(request);

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(challengeService, times(1)).validateChallenge(nonce);
        verify(hmacService, times(1)).validateSignature(nonce, signature, deviceId);
        verify(jwtService, times(1)).generateToken(any(Authentication.class));
        verify(challengeService, times(1)).removeChallenge(nonce);
    }

    @Test
    @DisplayName("Deve rejeitar desafio inválido ou expirado")
    void authenticate_WithInvalidChallenge_ShouldThrowBusinessException() {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String signature = "valid-signature";
        String deviceId = "test-device-123";
        
        AuthenticationRequest request = new AuthenticationRequest(nonce, signature, deviceId);
        
        when(challengeService.validateChallenge(nonce)).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authenticationService.authenticate(request);
        });
        
        assertEquals("Invalid or expired challenge", exception.getMessage());
        verify(challengeService, times(1)).validateChallenge(nonce);
        verify(hmacService, never()).validateSignature(any(), any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve rejeitar assinatura inválida")
    void authenticate_WithInvalidSignature_ShouldThrowBusinessException() {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String signature = "invalid-signature";
        String deviceId = "test-device-123";
        
        AuthenticationRequest request = new AuthenticationRequest(nonce, signature, deviceId);
        
        when(challengeService.validateChallenge(nonce)).thenReturn(true);
        when(hmacService.validateSignature(nonce, signature, deviceId)).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authenticationService.authenticate(request);
        });
        
        assertEquals("Invalid signature", exception.getMessage());
        verify(challengeService, times(1)).validateChallenge(nonce);
        verify(hmacService, times(1)).validateSignature(nonce, signature, deviceId);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve suportar método legado de autenticação")
    void authenticate_WithLegacyMethod_ShouldReturnJwtToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MjM5MDIyfQ.1234567890";
        when(jwtService.generateToken(authentication)).thenReturn(expectedToken);

        // Act
        String actualToken = authenticationService.authenticate(authentication);

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(jwtService, times(1)).generateToken(authentication);
    }
}
