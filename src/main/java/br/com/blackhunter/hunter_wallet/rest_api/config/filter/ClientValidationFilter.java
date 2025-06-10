/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.config.filter;

import br.com.blackhunter.hunter_wallet.rest_api.core.validation.ClientCredentialsValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <p>Classe <code>ClientValidationFilter</code>.</p>
 * <p>Filtro para validação de clientes que acessam a API.</p>
 * <p>Este filtro valida os headers de identificação do dispositivo (X-Device-ID, X-APP-Signature e X-Nonce), exceto para endpoints públicos.</p>
 * 
 * @since 1.0.0
 */
@Component
@Order(0) // Define a ordem de execução do filtro (antes do JwtAuthenticationFilter que tem ordem 1)
public class ClientValidationFilter extends OncePerRequestFilter {
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // Lista de endpoints públicos que não requerem validação de cliente
    private final List<String> publicEndpoints = Arrays.asList(
            "/v1/public/challenge"
    );
    
    // Padrão para endpoints de desenvolvimento
    private final String devEndpointsPattern = "/v1/dev/**";
    
    @Autowired
    private Environment environment;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Verifica se é um endpoint público que não requer validação
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Para todos os outros endpoints, valida os headers do cliente
        String deviceId = request.getHeader("X-Device-ID");
        String appSignature = request.getHeader("X-APP-Signature");
        String nonce = request.getHeader("X-Nonce");

        // Validação do Device ID
        if (deviceId == null || deviceId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Device-ID header");
            return;
        }

        // Validação da assinatura do app
        if (appSignature == null || appSignature.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-APP-Signature header");
            return;
        }

        // Validação do nonce
        if (nonce == null || nonce.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Nonce header");
            return;
        }
        
        // Validação do formato do nonce (deve ser um UUID válido)
        try {
            UUID.fromString(nonce);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-Nonce format");
            return;
        }

        // Verifica se estamos em ambiente de desenvolvimento
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        
        // Em ambiente de produção, valida as credenciais do cliente
        if (!isDev && (!ClientCredentialsValidation.validateDeviceId(deviceId) || 
                       !ClientCredentialsValidation.validateAppSignature(appSignature))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid deviceId or appSignature");
            return;
        }
        
        // Continua com a cadeia de filtros se a validação passar
        filterChain.doFilter(request, response);
    }
    
    /**
     * <p>Verifica se o caminho da requisição é um endpoint público ou de desenvolvimento.</p>
     * <p>Endpoints públicos e de desenvolvimento não requerem validação de cliente.</p>
     * 
     * @param requestPath caminho da requisição
     * @return true se for um endpoint público ou de desenvolvimento, false caso contrário
     */
    private boolean isPublicEndpoint(String requestPath) {
        // Verifica se é um endpoint de desenvolvimento
        if (pathMatcher.match(devEndpointsPattern, requestPath)) {
            return true;
        }
        
        // Verifica se é um endpoint público
        return publicEndpoints.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }
}
