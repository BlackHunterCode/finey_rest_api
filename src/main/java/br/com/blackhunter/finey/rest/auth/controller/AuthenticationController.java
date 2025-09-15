/*
 * @(#)AuthenticationController.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.auth.controller;

import br.com.blackhunter.finey.rest.auth.dto.AuthenticationRequest;
import br.com.blackhunter.finey.rest.auth.dto.ChallengeResponse;
import br.com.blackhunter.finey.rest.auth.service.AuthenticationService;
import br.com.blackhunter.finey.rest.core.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Classe <code>AuthenticationController</code>.</p>
 * <p>Controlador responsável pelo processo de autenticação baseado em challenge-response.</p>
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1/public")
public class AuthenticationController {
    private static final String SUCCESS = "success";
    private static final int SUCCESS_CODE = HttpStatus.OK.value();
    
    private final AuthenticationService authenticationService;

    /**
     * <p>Construtor para <code>AuthenticationController</code>.</p>
     * 
     * @param authenticationService serviço de autenticação
     */
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * <p>Endpoint para obter um novo desafio de autenticação.</p>
     * 
     * @return resposta contendo o nonce e timestamp de expiração
     */
    @GetMapping("/challenge")
    public ResponseEntity<ApiResponse<ChallengeResponse>> getChallenge() {
        ChallengeResponse challenge = authenticationService.getChallenge();
        return ResponseEntity.ok(new ApiResponse<>(SUCCESS, SUCCESS_CODE, challenge));
    }

    /**
     * <p>Endpoint para autenticação com resposta ao desafio.</p>
     * <p>O deviceId, nonce e a assinatura são obtidos dos headers da requisição.</p>
     * 
     * @param request objeto contendo email e password
     * @param deviceId identificador do dispositivo obtido do header X-Device-ID
     * @param signature assinatura HMAC obtida do header X-APP-Signature
     * @param nonce UUID do desafio obtido do header X-Nonce
     * @return token JWT para autenticação subsequente
     */
    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<String>> authenticate(
            @Valid @RequestBody AuthenticationRequest request,
            @RequestHeader("X-Device-ID") String deviceId,
            @RequestHeader("X-APP-Signature") String signature,
            @RequestHeader("X-Nonce") String nonce) {
        
        String token = authenticationService.authenticate(request, deviceId, signature, nonce);
        return ResponseEntity.ok(new ApiResponse<>(SUCCESS, SUCCESS_CODE, token));
    }
}
