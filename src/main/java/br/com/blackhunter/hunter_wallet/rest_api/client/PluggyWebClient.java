/*
 * @(#)PluggyWebClient.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.client;

import org.springframework.stereotype.Component;

@Component
public class PluggyWebClient {
    public String getAccessToken(String clientId, String clientSecret) {
        return "access_token_placeholder";
    }

    public boolean testAccessToken(String token) {
        return true;
    }
}
