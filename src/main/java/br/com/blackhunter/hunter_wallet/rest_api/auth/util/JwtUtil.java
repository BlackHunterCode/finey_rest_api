package br.com.blackhunter.hunter_wallet.rest_api.auth.util;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.HttpContextData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtUtil {
    private final UserAccountService userAccountService;

    public JwtUtil(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    public UserAccountEntity getUserAccountFromToken() {
        String extractedToken = extractAuthorizationTokenFromRequest();
        UUID userId =  UUID.randomUUID(); //extractUserIdFromToken(token);
        return userAccountService.findEntityById(userId);
    }

    public String extractAuthorizationTokenFromRequest() {
        HttpServletRequest request = HttpContextData.getCurrentRequest();
        String authHeader = request.getHeader("Authorization");
        return authHeader.replace("Bearer token ", "");
    }
}
