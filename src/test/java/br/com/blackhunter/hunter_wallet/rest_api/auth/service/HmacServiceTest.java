/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Classe <code>HmacServiceTest</code>.</p>
 * <p>Testes unitários para o serviço de validação HMAC.</p>
 * 
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class HmacServiceTest {

    @InjectMocks
    private HmacService hmacService;

    private static final String TEST_DEVICE_ID = "test-device-123";
    private static final String TEST_SECRET = "TestSecretKey123";
    private static final String DEFAULT_SECRET = "BlackHunterDefaultSecret";

    @BeforeEach
    void setUp() {
        // Set default secret via reflection
        ReflectionTestUtils.setField(hmacService, "defaultSecret", DEFAULT_SECRET);
    }

    @Test
    @DisplayName("Deve validar assinatura HMAC válida com segredo padrão")
    void validateSignature_WithValidSignatureAndDefaultSecret_ShouldReturnTrue() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String signature = calculateHmac(nonce.toString(), DEFAULT_SECRET);

        // Act
        boolean isValid = hmacService.validateSignature(nonce, signature, TEST_DEVICE_ID);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve validar assinatura HMAC válida com segredo personalizado")
    void validateSignature_WithValidSignatureAndCustomSecret_ShouldReturnTrue() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        hmacService.registerDeviceSecret(TEST_DEVICE_ID, TEST_SECRET);
        String signature = calculateHmac(nonce.toString(), TEST_SECRET);

        // Act
        boolean isValid = hmacService.validateSignature(nonce, signature, TEST_DEVICE_ID);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar assinatura HMAC inválida")
    void validateSignature_WithInvalidSignature_ShouldReturnFalse() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String invalidSignature = "invalid-signature";

        // Act
        boolean isValid = hmacService.validateSignature(nonce, invalidSignature, TEST_DEVICE_ID);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar assinatura HMAC com segredo incorreto")
    void validateSignature_WithWrongSecret_ShouldReturnFalse() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        hmacService.registerDeviceSecret(TEST_DEVICE_ID, TEST_SECRET);
        
        // Calcular assinatura com segredo diferente
        String wrongSignature = calculateHmac(nonce.toString(), "WrongSecret");

        // Act
        boolean isValid = hmacService.validateSignature(nonce, wrongSignature, TEST_DEVICE_ID);

        // Assert
        assertFalse(isValid);
    }

    /**
     * Método auxiliar para calcular HMAC-SHA256
     */
    private String calculateHmac(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
}
