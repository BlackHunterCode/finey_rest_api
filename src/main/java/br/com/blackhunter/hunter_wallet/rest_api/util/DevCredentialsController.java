/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.util;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.ApiResponse;
import br.com.blackhunter.hunter_wallet.rest_api.core.util.AppContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Classe <code>DevCredentialsController</code>.</p>
 * <p>Controlador REST para geração e disponibilização de credenciais de dispositivo para desenvolvedores.</p>
 * <p>Este controlador só está disponível em ambiente de desenvolvimento.</p>
 *
 * @author BlackHunter
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1/dev/credentials")
public class DevCredentialsController {

    private final DeviceCredentialsGenerator credentialsGenerator;
    
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public DevCredentialsController(DeviceCredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    /**
     * <p>Gera novas credenciais de dispositivo para desenvolvimento.</p>
     * <p>Este endpoint só está disponível em ambiente de desenvolvimento.</p>
     *
     * @return resposta contendo as credenciais geradas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> generateCredentials() {
        // Verificar se está em ambiente de desenvolvimento
        if (!AppContextUtil.isDevelopmentMode(activeProfile)) {
            return ResponseEntity.notFound().build();
        }

        // Gerar novas credenciais
        credentialsGenerator.generateDeviceCredentials();

        // Criar resposta
        Map<String, String> credentials = new HashMap<>();
        credentials.put("deviceId", credentialsGenerator.getDeviceId());
        credentials.put("appSignature", credentialsGenerator.getAppSignature());
        credentials.put("signatureKey", credentialsGenerator.getSignatureKey());

        ApiResponse<Map<String, String>> response = new ApiResponse<>();
        response.setStatus("success");
        response.setStatusCode(200);
        response.setData(credentials);
        return ResponseEntity.ok(response);
    }
}
