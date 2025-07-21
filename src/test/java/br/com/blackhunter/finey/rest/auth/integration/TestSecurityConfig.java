/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.finey.rest.auth.integration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <p>Classe <code>TestSecurityConfig</code>.</p>
 * <p>Configuração de segurança específica para testes de integração.</p>
 * <p>Esta classe desabilita a configuração de segurança padrão e permite todas as requisições durante os testes.</p>
 *
 * @author BlackHunter
 * @version 1.0.0
 * @since 1.0.0
 */
@TestConfiguration
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
@Profile("test")
@Order(Integer.MIN_VALUE) // Garante que esta configuração seja aplicada antes de qualquer outra
@Configuration
public class TestSecurityConfig {

    /**
     * <p>Configura o SecurityFilterChain para testes.</p>
     * <p>Desabilita CSRF e permite todas as requisições para facilitar os testes.</p>
     * 
     * @param http configuração de segurança HTTP
     * @return SecurityFilterChain configurado para testes
     * @throws Exception se ocorrer algum erro durante a configuração
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        // Desabilitar completamente a segurança para testes
        http
            .securityMatcher("/**") // Especifica explicitamente o matcher
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
