/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * <p>Classe <code>AuthenticationRequest</code>.</p>
 * <p>DTO que representa a requisição de autenticação com resposta ao desafio.</p>
 * 
 * @since 1.0.0
 */
public class AuthenticationRequest {
    @NotNull(message = "Nonce is required")
    private UUID nonce;
    
    @NotBlank(message = "Response signature is required")
    private String signature;
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;

    /**
     * <p>Construtor padrão para <code>AuthenticationRequest</code>.</p>
     */
    public AuthenticationRequest() {
    }

    /**
     * <p>Construtor para <code>AuthenticationRequest</code>.</p>
     * 
     * @param nonce UUID do desafio original
     * @param signature assinatura HMAC gerada pelo cliente
     * @param deviceId identificador único do dispositivo
     */
    public AuthenticationRequest(UUID nonce, String signature, String deviceId) {
        this.nonce = nonce;
        this.signature = signature;
        this.deviceId = deviceId;
    }

    /**
     * <p>Obtém o nonce do desafio.</p>
     * 
     * @return UUID do desafio
     */
    public UUID getNonce() {
        return nonce;
    }

    /**
     * <p>Define o nonce do desafio.</p>
     * 
     * @param nonce UUID do desafio
     */
    public void setNonce(UUID nonce) {
        this.nonce = nonce;
    }

    /**
     * <p>Obtém a assinatura HMAC.</p>
     * 
     * @return assinatura gerada pelo cliente
     */
    public String getSignature() {
        return signature;
    }

    /**
     * <p>Define a assinatura HMAC.</p>
     * 
     * @param signature assinatura gerada pelo cliente
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * <p>Obtém o ID do dispositivo.</p>
     * 
     * @return identificador único do dispositivo
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * <p>Define o ID do dispositivo.</p>
     * 
     * @param deviceId identificador único do dispositivo
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
