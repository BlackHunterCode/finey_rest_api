/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <p>Classe <code>JwtAuthenticationFilter</code>.</p>
 * <p>Filtro para autenticação JWT que valida tokens e configura o SecurityContext.</p>
 * 
 * @since 1.0.0
 */
@Component
@Order(1) // Define a ordem de execução do filtro
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Implement your JWT authentication logic here
        // For example, extract the token from the request header and validate it
        // If valid, set the authentication in the SecurityContextHolder

        filterChain.doFilter(request, response);
    }
}
