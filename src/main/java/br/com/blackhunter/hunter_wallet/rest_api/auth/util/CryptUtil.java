package br.com.blackhunter.hunter_wallet.rest_api.auth.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CryptUtil {
    /**
     * Criptografa uma string usando o algoritmo AES.
     *
     * @param data String a ser criptografada
     * @param secretKey Chave secreta para criptografia (deve ter exatamente 16, 24 ou 32 bytes)
     * @return String criptografada em formato Base64
     * @throws Exception se ocorrer algum erro durante a criptografia
     */
    public static String encrypt(String data, String secretKey) throws Exception {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(keyBytes, 0, 16, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Descriptografa uma string usando o algoritmo AES.
     *
     * @param encryptedData String criptografada em formato Base64
     * @param secretKey Chave secreta para descriptografia (deve ser a mesma usada na criptografia)
     * @return String descriptografada
     * @throws Exception se ocorrer algum erro durante a descriptografia
     */
    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        // Garante que a chave tenha o tamanho correto (16 bytes para AES-128)
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(keyBytes, 0, 16, "AES");

        // Inicializa o cipher para descriptografia
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decodifica o Base64 e descriptografa
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        // Converte de volta para string
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Verifica se uma string está criptografada usando o algoritmo AES implementado nesta classe.
     * 
     * @param text String a ser verificada
     * @param secretKey Chave secreta usada para tentar descriptografar
     * @return true se a string foi criptografada com o método encrypt() desta classe, false caso contrário
     */
    public static boolean isEncrypted(String text, String secretKey) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        try {
            // Tenta descriptografar o texto
            String decrypted = decrypt(text, secretKey);
            
            // Se chegou até aqui sem lançar exceção, o texto estava criptografado
            // com o algoritmo AES e a chave fornecida
            return true;
        } catch (Exception e) {
            // Se ocorrer qualquer exceção durante a descriptografia,
            // o texto não está criptografado com o algoritmo AES desta classe
            // ou a chave está incorreta
            return false;
        }
    }
}
