/*
 * @(#)PluggyWebClient.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PluggyWebClient {
    private static final String AUTH_URL = "https://api.pluggy.ai/auth";
    private final RestTemplate restTemplate;

    public PluggyWebClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken(String clientId, String clientSecret) {
        // Configurar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");

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

        return response != null ? response.getApiKey() : null;
    }

    public boolean testAccessToken(String tokenEncrypted) {
        return true;
    }

    @Data
    @NoArgsConstructor
    private static class PluggyAuthResponse {
        private String apiKey;
    }
}
