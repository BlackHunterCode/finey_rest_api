/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.finey.rest.auth.controller;

import br.com.blackhunter.finey.rest.auth.dto.AuthenticationRequest;
import br.com.blackhunter.finey.rest.auth.dto.ChallengeResponse;
import br.com.blackhunter.finey.rest.auth.service.AuthenticationService;
import br.com.blackhunter.finey.rest.core.dto.ApiResponse;
import br.com.blackhunter.finey.rest.core.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>Classe <code>AuthenticationControllerTest</code>.</p>
 * <p>Testes unitários para o controlador de autenticação.</p>
 * 
 * @since 1.0.0
 */
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Deve retornar um desafio com nonce e timestamp de expiração")
    void getChallenge_ShouldReturnChallengeWithNonceAndExpiration() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        long expiresAt = System.currentTimeMillis() + 300000; // 5 minutos
        ChallengeResponse challenge = new ChallengeResponse(nonce, expiresAt);
        
        when(authenticationService.getChallenge()).thenReturn(challenge);

        // Act & Assert
        mockMvc.perform(get("/v1/public/challenge")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.nonce").isNotEmpty())
                .andExpect(jsonPath("$.data.expiresAt").isNumber());
    }

    @Test
    @DisplayName("Deve autenticar com resposta ao desafio válida e retornar token JWT")
    void authenticate_WithValidChallengeResponse_ShouldReturnJwtToken() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String nonceStr = nonce.toString();
        String signature = "valid-signature";
        String deviceId = "test-device-123";
        String email = "test@example.com";
        String password = "password123";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MjM5MDIyfQ.1234567890";
        
        AuthenticationRequest request = new AuthenticationRequest(email, password);
        
        when(authenticationService.authenticate(any(AuthenticationRequest.class), 
                                              any(String.class), 
                                              any(String.class),
                                              any(String.class)))
            .thenReturn(expectedToken);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/v1/public/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Device-ID", deviceId)
                .header("X-APP-Signature", signature)
                .header("X-Nonce", nonceStr)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").isString())
                .andReturn();
                
        ApiResponse<?> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, String.class));
                
        assertNotNull(response.getData());
        assertEquals(expectedToken, response.getData());
    }

    @Test
    @DisplayName("Deve tratar falha de autenticação por desafio inválido")
    void authenticate_WithInvalidChallenge_ShouldReturnError() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String nonceStr = nonce.toString();
        String signature = "valid-signature";
        String deviceId = "test-device-123";
        String email = "test@example.com";
        String password = "password123";
        
        AuthenticationRequest request = new AuthenticationRequest(email, password);
        
        when(authenticationService.authenticate(any(AuthenticationRequest.class), 
                                              any(String.class), 
                                              any(String.class),
                                              any(String.class)))
                .thenThrow(new BusinessException("Invalid or expired challenge"));

        // Act & Assert
        mockMvc.perform(post("/v1/public/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Device-ID", deviceId)
                .header("X-APP-Signature", signature)
                .header("X-Nonce", nonceStr)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
    
    @Test
    @DisplayName("Deve tratar falha de autenticação por assinatura inválida")
    void authenticate_WithInvalidSignature_ShouldReturnError() throws Exception {
        // Arrange
        UUID nonce = UUID.randomUUID();
        String nonceStr = nonce.toString();
        String signature = "invalid-signature";
        String deviceId = "test-device-123";
        String email = "test@example.com";
        String password = "password123";
        
        AuthenticationRequest request = new AuthenticationRequest(email, password);
        
        when(authenticationService.authenticate(any(AuthenticationRequest.class), 
                                              any(String.class), 
                                              any(String.class),
                                              any(String.class)))
                .thenThrow(new BusinessException("Invalid signature"));

        // Act & Assert
        mockMvc.perform(post("/v1/public/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Device-ID", deviceId)
                .header("X-APP-Signature", signature)
                .header("X-Nonce", nonceStr)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}
