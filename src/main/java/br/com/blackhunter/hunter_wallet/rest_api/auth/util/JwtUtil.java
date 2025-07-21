/*
 * @(#)JwtUtil.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.auth.util;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.HttpContextData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
    private final UserAccountService userAccountService;
    private final JwtDecoder jwtDecoder;

    public JwtUtil(
            UserAccountService userAccountService,
            JwtDecoder jwtDecoder
    ) {
        this.userAccountService = userAccountService;
        this.jwtDecoder         = jwtDecoder;
    }

    public UserAccountEntity getUserAccountFromToken() {
        String extractedToken = extractAuthorizationTokenFromRequest();
        if(extractedToken == null || extractedToken.isEmpty()) {
            throw new IllegalArgumentException("Authorization token is missing or empty");
        }
        UUID userId = extractUserIdFromToken(extractedToken);
        return userAccountService.findEntityById(userId);
    }

    public String extractAuthorizationTokenFromRequest() {
        HttpServletRequest request = HttpContextData.getCurrentRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            throw new IllegalArgumentException("Authorization header is missing or empty");
        }
        // Standard Bearer token format is "Bearer [token]"
        return authHeader.replace("Bearer ", "");
    }

    public boolean isExpired(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is missing or empty");
        }
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getExpiresAt().isBefore(java.time.Instant.now());
    }

    private UUID extractUserIdFromToken(String token) {
        Jwt decodedJwt = jwtDecoder.decode(token);
        String email = decodedJwt.getClaim("sub");
        return userAccountService.findUserIdByEmail(email);
    }
}