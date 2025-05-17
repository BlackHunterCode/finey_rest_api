/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * <p>Classe <code>DeviceCredentialsGenerator</code>.</p>
 * <p>Utilitário para geração de credenciais de dispositivo (DeviceID e AppSignature) para desenvolvimento e testes.</p>
 * <p>Em ambiente de desenvolvimento, gera e exibe credenciais nos logs para facilitar o desenvolvimento e testes.</p>
 *
 * @author BlackHunter
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class DeviceCredentialsGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceCredentialsGenerator.class);
    private static final String ALGORITHM = "HmacSHA256";
    private static final int SIGNATURE_KEY_LENGTH = 32; // 256 bits
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;
    
    @Value("${app.dev.generate-credentials:false}")
    private boolean generateCredentials;
    
    private String deviceId;
    private String appSignature;
    private String signatureKey;
    
    /**
     * <p>Inicializa o gerador de credenciais após a construção do bean.</p>
     * <p>Se estiver no ambiente de desenvolvimento e a geração de credenciais estiver habilitada,
     * gera e exibe as credenciais nos logs.</p>
     */
    @PostConstruct
    public void init() {
        if (isDevelopmentMode() && generateCredentials) {
            generateDeviceCredentials();
            logDeviceCredentials();
        }
    }
    
    /**
     * <p>Gera credenciais de dispositivo (DeviceID e AppSignature).</p>
     */
    public void generateDeviceCredentials() {
        // Gerar DeviceID único baseado em UUID
        deviceId = "dev-" + UUID.randomUUID().toString();
        
        // Gerar chave secreta para assinatura
        signatureKey = generateRandomKey(SIGNATURE_KEY_LENGTH);
        
        // Gerar assinatura do aplicativo usando HMAC-SHA256
        appSignature = calculateHmac(deviceId, signatureKey);
    }
    
    /**
     * <p>Exibe as credenciais geradas nos logs.</p>
     */
    private void logDeviceCredentials() {
        logger.info("==================== DEVICE CREDENTIALS ====================");
        logger.info("DeviceID: {}", deviceId);
        logger.info("AppSignature: {}", appSignature);
        logger.info("SignatureKey (para HMAC): {}", signatureKey);
        logger.info("============================================================");
        logger.info("Use estas credenciais para testar a API em ambiente de desenvolvimento.");
        logger.info("Adicione os seguintes headers em todas as requisições:");
        logger.info("X-Device-ID: {}", deviceId);
        logger.info("X-APP-Signature: {}", appSignature);
        logger.info("============================================================");
    }
    
    /**
     * <p>Verifica se a aplicação está rodando em modo de desenvolvimento.</p>
     * 
     * @return true se estiver em modo de desenvolvimento, false caso contrário
     */
    private boolean isDevelopmentMode() {
        return activeProfile.contains("dev") || activeProfile.contains("local");
    }
    
    /**
     * <p>Gera uma chave aleatória com o tamanho especificado.</p>
     * 
     * @param length tamanho da chave em bytes
     * @return chave aleatória codificada em Base64
     */
    private String generateRandomKey(int length) {
        byte[] key = new byte[length];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
    
    /**
     * <p>Calcula o HMAC-SHA256 dos dados usando a chave fornecida.</p>
     * 
     * @param data dados a serem assinados
     * @param key chave para assinatura
     * @return assinatura HMAC em formato Base64
     */
    private String calculateHmac(String data, String key) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            logger.error("Erro ao calcular HMAC: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar assinatura do aplicativo", e);
        }
    }
    
    /**
     * <p>Obtém o DeviceID gerado.</p>
     * 
     * @return DeviceID gerado
     */
    public String getDeviceId() {
        return deviceId;
    }
    
    /**
     * <p>Obtém a AppSignature gerada.</p>
     * 
     * @return AppSignature gerada
     */
    public String getAppSignature() {
        return appSignature;
    }
    
    /**
     * <p>Obtém a chave de assinatura gerada.</p>
     * 
     * @return chave de assinatura
     */
    public String getSignatureKey() {
        return signatureKey;
    }
}
