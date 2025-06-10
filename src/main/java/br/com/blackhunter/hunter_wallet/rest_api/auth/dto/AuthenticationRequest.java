/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * <p>Classe <code>AuthenticationRequest</code>.</p>
 * <p>DTO que representa a requisição de autenticação com resposta ao desafio.</p>
 * <p>Os campos deviceId, signature e nonce foram movidos para os headers da requisição.</p>
 * 
 * @since 1.0.0
 */
public class AuthenticationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * <p>Construtor padrão para <code>AuthenticationRequest</code>.</p>
     */
    public AuthenticationRequest() {
    }

    /**
     * <p>Construtor para <code>AuthenticationRequest</code>.</p>
     * 
     * @param email email do usuário (username)
     * @param password senha do usuário
     */
    public AuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * <p>Obtém o email do usuário.</p>
     * 
     * @return email do usuário
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Define o email do usuário.</p>
     * 
     * @param email email do usuário
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * <p>Obtém a senha do usuário.</p>
     * 
     * @return senha do usuário
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Define a senha do usuário.</p>
     * 
     * @param password senha do usuário
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
