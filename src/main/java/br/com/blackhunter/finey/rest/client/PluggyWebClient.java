/*
 * @(#)PluggyWebClient.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.client;

import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionStatus;
import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionType;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyAccountDataEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class PluggyWebClient {
    private static final String PLUGGY_API_BASE_URL = "https://api.pluggy.ai";
    private static final String AUTH_URL = PLUGGY_API_BASE_URL + "/auth";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final int PAGE_SIZE = 500; // Máximo permitido pela API Pluggy


    public PluggyWebClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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

    public List<PluggyAccount> getAccountIdsByItemId(String itemId, String accessToken) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID não pode ser nulo ou vazio");
        }
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token não pode ser nulo ou vazio");
        }

        try {
            // Same validation as above...

            String url = PLUGGY_API_BASE_URL + "/accounts?itemId=" + itemId;
            log.debug("Tentando buscar contas na URL: {}", url);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            log.info("Enviando requisição para API Pluggy para itemId: {}", itemId);

            ResponseEntity<PluggyAccountsResponse> responseEntity;
            try {
                responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        PluggyAccountsResponse.class
                );
            } catch (HttpClientErrorException e) {
                log.error("Erro HTTP ao buscar contas: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                throw new RuntimeException("Erro na comunicação com a API Pluggy: " + e.getStatusCode(), e);
            }


            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to fetch accounts: " + responseEntity.getStatusCode());
            }

            PluggyAccountsResponse response = responseEntity.getBody();
            if (response == null || response.getResults() == null) {
                return Collections.emptyList();
            }

            return response.getResults();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch accounts", e);
        }
    }

    public List<PluggyTransaction> getAllTransactionsPeriodByOriginalAccountId(
            final String accessToken,
            final String originalAccountId,
            final LocalDate startDate,
            final LocalDate endDate) {

        List<PluggyTransaction> allTransactions = new ArrayList<>();
        int currentPage = 1;
        boolean hasMore = true;

        try {
            while (hasMore) {
                System.out.println("accountId: " + originalAccountId);
                String url = buildTransactionsUrl(originalAccountId, startDate, endDate, currentPage);

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-API-KEY", accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Failed to fetch transactions: " +
                            response.getStatusCode() + " - " + response.getBody());
                }

                Map<String, Object> responseMap = objectMapper.readValue(
                        response.getBody(), new TypeReference<Map<String, Object>>() {});

                List<Map<String, Object>> results = (List<Map<String, Object>>) responseMap.get("results");
                List<PluggyTransaction> pageTransactions = new ArrayList<>();

                for (Map<String, Object> result : results) {
                    PluggyTransaction transaction = objectMapper.convertValue(result, PluggyTransaction.class);
                    pageTransactions.add(transaction);
                }

                allTransactions.addAll(pageTransactions);

                // Verifica se há mais páginas
                int total = (int) responseMap.get("total");
                hasMore = (currentPage * PAGE_SIZE) < total;
                currentPage++;

                // Pequena pausa para evitar rate limiting
                Thread.sleep(200);
            }

            System.out.println("Pluggy Transactions fetched: " + allTransactions.size());
            System.out.println("Pluggy Transactions: " + allTransactions);

            return allTransactions;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching transactions: " + e.getMessage(), e);
        }
    }


    /* Métodos/Classes privados. */

    private String buildTransactionsUrl(String accountId, LocalDate startDate, LocalDate endDate, int page) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return UriComponentsBuilder.fromHttpUrl("https://api.pluggy.ai/transactions")
                .queryParam("accountId", accountId)
                .queryParam("dateFrom", startDate.format(formatter))
                .queryParam("dateTo", endDate.format(formatter))
                .queryParam("page", page)
                .queryParam("pageSize", PAGE_SIZE)
                .toUriString();
    }

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

    @Data
    @NoArgsConstructor
    private static class PluggyAccountsResponse {
        private List<PluggyAccount> results;
    }

    @Data
    @NoArgsConstructor
    public static class PluggyAccount {
        private String id;
        private String name;
        private String type;
        private Double balance;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluggyTransaction {
        private String id;
        private String description;
        private Double amount;
        private String currencyCode;
        private String category;
        private Date date;
        private String reference;
        private String type; // "CREDIT" ou "DEBIT"
        private String accountId;
        private Double balance;
        private String descriptionRaw;
        private String merchantName;
        private String status;

        /**
         * Converte esta transação da Pluggy para uma TransactionEntity
         *
         * @return TransactionEntity populada
         */
        public TransactionEntity toTransactionEntity() {
            log.debug("Convertendo transação Pluggy: {}", this.id);

            Objects.requireNonNull(this.id, "Transaction ID cannot be null");
            Objects.requireNonNull(this.type, "Transaction type cannot be null");

            TransactionEntity entity = new TransactionEntity();

            // Definir os valores básicos
            entity.setUserAccount(null);
            entity.setPluggyAccountId(null);
            entity.setAmount(convertMonetaryValue(this.amount));
            entity.setBalance(convertMonetaryValue(this.balance));
            entity.setDescription(this.description != null ? this.description : "");
            entity.setDescriptionRaw(this.descriptionRaw != null ? this.descriptionRaw : this.description);
            entity.setCurrencyCode(this.currencyCode != null ? this.currencyCode : "BRL");
            entity.setCategory(this.category);
            entity.setProviderTransactionCode(this.reference);
            entity.setProviderTransactionId(this.id);

            // Converter o tipo da transação
            if ("CREDIT".equalsIgnoreCase(this.type)) {
                entity.setType(TransactionType.CREDIT);
            } else {
                entity.setType(TransactionType.DEBIT);
            }

            // Converter o status
            if ("POSTED".equalsIgnoreCase(this.status)) {
                entity.setStatus(TransactionStatus.POSTED);
            } else if ("PENDING".equalsIgnoreCase(this.status)) {
                entity.setStatus(TransactionStatus.PENDING);
            } else {
                entity.setStatus(TransactionStatus.UNKNOWN);
            }

            // Converter a data
            if (this.date != null) {
                entity.setTransactionDate(LocalDateTime.ofInstant(
                        this.date.toInstant(), ZoneId.systemDefault()));
            } else {
                entity.setTransactionDate(LocalDateTime.now());
            }

            entity.setTransactionLocalDate(entity.getTransactionDate().toLocalDate());

            entity.setCreatedAt(LocalDateTime.now());

            return entity;
        }

        private BigDecimal convertMonetaryValue(Double value) {
            if (value == null) return BigDecimal.ZERO;

            try {
                // Mantém o sinal original (-/+)
                return BigDecimal.valueOf(value)
                        .setScale(2, RoundingMode.HALF_UP);
            } catch (Exception e) {
                log.error("Valor monetário inválido: {}", value, e);
                return BigDecimal.ZERO;
            }
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        return headers;
    }
}
