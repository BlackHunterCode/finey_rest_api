/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.integration;

import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.AuthenticationRequest;
import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.ChallengeResponse;
import br.com.blackhunter.hunter_wallet.rest_api.core.dto.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>Classe <code>AuthenticationFlowIT</code>.</p>
 * <p>Testes de integração para o fluxo completo de autenticação com challenge-response.</p>
 * 
 * @since 1.0.0
 */
@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${auth.hmac.default-secret:TestSecretForIntegrationTests}")
    private String defaultSecret;

    private static final String TEST_DEVICE_ID = "test-device-integration";

    @Test
    @DisplayName("Deve obter um desafio com nonce e timestamp de expiração")
    void getChallenge_ShouldReturnChallengeWithNonceAndExpiration() throws Exception {
        // Act
        MvcResult result = mockMvc.perform(get("/v1/public/challenge")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.nonce").isNotEmpty())
                .andExpect(jsonPath("$.data.expiresAt").isNumber())
                .andReturn();

        // Assert
        String content = result.getResponse().getContentAsString();
        ApiResponse<ChallengeResponse> response = objectMapper.readValue(content, 
                new TypeReference<ApiResponse<ChallengeResponse>>() {});
        
        assertNotNull(response.getData());
        assertNotNull(response.getData().getNonce());
        assertTrue(response.getData().getExpiresAt() > System.currentTimeMillis());
    }

    @Test
    @DisplayName("Deve completar o fluxo completo de autenticação challenge-response")
    void completeAuthenticationFlow_ShouldSucceed() throws Exception {
        // Passo 1: Obter o desafio
        MvcResult challengeResult = mockMvc.perform(get("/v1/public/challenge")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ChallengeResponse> challengeResponse = objectMapper.readValue(
                challengeResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ChallengeResponse>>() {});
        
        UUID nonce = challengeResponse.getData().getNonce();
        
        // Passo 2: Calcular a assinatura HMAC
        String signature = calculateHmac(nonce.toString(), defaultSecret);
        
        // Passo 3: Enviar a resposta ao desafio
        AuthenticationRequest authRequest = new AuthenticationRequest(nonce, signature, TEST_DEVICE_ID);
        
        MvcResult authResult = mockMvc.perform(post("/v1/public/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn();
        
        // Verificar o token JWT retornado
        ApiResponse<String> tokenResponse = objectMapper.readValue(
                authResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<String>>() {});
        
        String token = tokenResponse.getData();
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verificar estrutura do token JWT (3 partes separadas por pontos)
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com nonce inválido")
    void authenticate_WithInvalidNonce_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UUID invalidNonce = UUID.randomUUID(); // Nonce que não foi gerado pelo servidor
        String signature = calculateHmac(invalidNonce.toString(), defaultSecret);
        AuthenticationRequest authRequest = new AuthenticationRequest(invalidNonce, signature, TEST_DEVICE_ID);
        
        // Act & Assert
        mockMvc.perform(post("/v1/public/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com assinatura inválida")
    void authenticate_WithInvalidSignature_ShouldReturnBadRequest() throws Exception {
        // Passo 1: Obter o desafio
        MvcResult challengeResult = mockMvc.perform(get("/v1/public/challenge")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<ChallengeResponse> challengeResponse = objectMapper.readValue(
                challengeResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ChallengeResponse>>() {});
        
        UUID nonce = challengeResponse.getData().getNonce();
        
        // Usar uma assinatura inválida
        String invalidSignature = "invalid-signature-value";
        AuthenticationRequest authRequest = new AuthenticationRequest(nonce, invalidSignature, TEST_DEVICE_ID);
        
        // Act & Assert
        mockMvc.perform(post("/v1/public/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
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
