/*
 * @(#)PluggyWebClient.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Component
public class PluggyWebClient {
    private static final String PLUGGY_API_BASE_URL = "https://api.pluggy.ai";
    private static final String AUTH_URL = PLUGGY_API_BASE_URL + "/auth";
    private final RestTemplate restTemplate;

    public PluggyWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @param clientId O Client ID fornecido pela Pluggy.
     * @param clientSecret O Client Secret fornecido pela Pluggy.
     *
     * <p>Método para obter o token de acesso da Pluggy.</p>
     * <p>Esse token é necessário para autenticar as requisições na API da Pluggy.</p>
     *
     * @return O token de acesso obtido da Pluggy.
     */
    public String getAccessToken(String clientId, String clientSecret) {
        // Configurar headers
        HttpHeaders headers = this.createHeaders();

        // Configurar payload
        Map<String, String> payload = new HashMap<>();
        payload.put("clientId", clientId);
        payload.put("clientSecret", clientSecret);

        // Criar a entidade HTTP com headers e payload
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        // Fazer a requisição POST e receber a resposta
        PluggyAuthResponse response = restTemplate.postForObject(
                AUTH_URL,
                request,
                PluggyAuthResponse.class
        );

        if (response == null || response.getApiKey() == null) {
            throw new RuntimeException("Failed to obtain access token from Pluggy API");
        }

        return response.getApiKey();
    }

    /**
     * @param token O token de acesso descriptografado.
     *
     * <p>Método para obter o Connect Token associado a um token de autenticação.</p>
     * <p>Esse token é necessário para conectarmos as credenciais bancárias do usuário.</p>
     *
     * @return O Connect Token associado ao token de autenticação fornecido.
     */
    public String getConnectToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }

            String url = PLUGGY_API_BASE_URL + "/connect_token";
        
            HttpHeaders headers = this.createHeaders();
            headers.set("X-API-KEY", token);
            

            HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

            try {
                System.out.println("Sending POST request to Pluggy API...");
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Failed to obtain Connect Token from Pluggy API: " + 
                        responseEntity.getStatusCode() + " - " + responseEntity.getBody());
                }

                // Parse the response manually to handle potential parsing issues
                String responseBody = responseEntity.getBody();
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    throw new RuntimeException("Empty response body from Pluggy API");
                }

                // Try to parse the access token from the response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);
                String accessToken = rootNode.path("accessToken").asText();
                
                if (accessToken == null || accessToken.trim().isEmpty()) {
                    throw new RuntimeException("No accessToken in response: " + responseBody);
                }

                return accessToken;
            } catch (Exception e) {
                System.err.println("Error in REST call to Pluggy API: " + e.getClass().getName() + ": " + e.getMessage());
                throw new RuntimeException("Failed to obtain Connect Token from Pluggy API: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Error in getConnectToken: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }
    /* Métodos/Classes privados. */

    @Data
    @NoArgsConstructor
    private static class PluggyAuthResponse {
        private String apiKey;
    }

    @Data
    @NoArgsConstructor
    private static class ConnectTokenResponse {
        private String accessToken;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        return headers;
    }
}
