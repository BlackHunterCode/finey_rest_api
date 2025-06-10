/*
 * @(#)JwtUtil.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.auth.util;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.HttpContextData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
        return authHeader.replace("Bearer token ", "");
    }

    private UUID extractUserIdFromToken(String token) {
        Jwt decodedJwt = jwtDecoder.decode(token);
        String email = decodedJwt.getClaim("sub");
        return userAccountService.findUserIdByEmail(email);
    }
}