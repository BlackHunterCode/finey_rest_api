/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Classe <code>HmacService</code>.</p>
 * <p>Serviço responsável por validar assinaturas HMAC para o processo de autenticação.</p>
 * 
 * @since 1.0.0
 */
@Service
public class HmacService {
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    // Mapa de segredos compartilhados por dispositivo
    // Em produção, isso seria armazenado em um banco de dados
    private final Map<String, String> deviceSecrets = new ConcurrentHashMap<>();
    
    @Value("${auth.hmac.default-secret:BlackHunterDefaultSecret}")
    private String defaultSecret;

    /**
     * <p>Valida a assinatura HMAC fornecida pelo cliente.</p>
     * 
     * @param nonce UUID do desafio
     * @param signature assinatura HMAC fornecida pelo cliente
     * @param deviceId identificador único do dispositivo
     * @return true se a assinatura for válida, false caso contrário
     */
    public boolean validateSignature(UUID nonce, String signature, String deviceId) {
        // Obtém o segredo compartilhado para o dispositivo
        String secret = getDeviceSecret(deviceId);
        
        // Calcula a assinatura esperada
        String expectedSignature = calculateHmac(nonce.toString(), secret);
        
        // Compara a assinatura fornecida com a esperada (comparação segura contra timing attacks)
        return isEqual(signature, expectedSignature);
    }

    /**
     * <p>Calcula o HMAC-SHA256 para o nonce usando o segredo compartilhado.</p>
     * 
     * @param data dados a serem assinados (nonce)
     * @param secret segredo compartilhado
     * @return assinatura HMAC em Base64
     */
    private String calculateHmac(String data, String secret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error calculating HMAC", e);
        }
    }

    /**
     * <p>Obtém o segredo compartilhado para um dispositivo.</p>
     * <p>Em produção, isso seria obtido de um banco de dados.</p>
     * 
     * @param deviceId identificador único do dispositivo
     * @return segredo compartilhado para o dispositivo
     */
    private String getDeviceSecret(String deviceId) {
        // Verifica se já temos um segredo para este dispositivo
        String secret = deviceSecrets.get(deviceId);
        
        // Se não existir, usa o segredo padrão
        // Em produção, isso seria obtido de um banco de dados
        if (secret == null) {
            return defaultSecret;
        }
        
        return secret;
    }

    /**
     * <p>Registra um novo segredo para um dispositivo.</p>
     * <p>Em produção, isso seria armazenado em um banco de dados.</p>
     * 
     * @param deviceId identificador único do dispositivo
     * @param secret segredo compartilhado
     */
    public void registerDeviceSecret(String deviceId, String secret) {
        deviceSecrets.put(deviceId, secret);
    }

    /**
     * <p>Compara duas strings de forma segura contra timing attacks.</p>
     * 
     * @param a primeira string
     * @param b segunda string
     * @return true se as strings forem iguais, false caso contrário
     */
    private boolean isEqual(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        
        return result == 0;
    }
}
