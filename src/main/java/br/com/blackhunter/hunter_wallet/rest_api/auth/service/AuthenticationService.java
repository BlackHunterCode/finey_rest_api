/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.service;

import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.AuthenticationRequest;
import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.ChallengeResponse;
import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * <p>Classe <code>AuthenticationService</code>.</p>
 * <p>Serviço responsável pelo processo de autenticação baseado em challenge-response.</p>
 * 
 * @since 1.0.0
 */
@Service
public class AuthenticationService {
    private final JwtService jwtService;
    private final ChallengeService challengeService;
    private final HmacService hmacService;

    /**
     * <p>Construtor para <code>AuthenticationService</code>.</p>
     * 
     * @param jwtService serviço para geração de tokens JWT
     * @param challengeService serviço para gerenciamento de desafios
     * @param hmacService serviço para validação de assinaturas HMAC
     */
    public AuthenticationService(JwtService jwtService, ChallengeService challengeService, HmacService hmacService) {
        this.jwtService = jwtService;
        this.challengeService = challengeService;
        this.hmacService = hmacService;
    }

    /**
     * <p>Gera um novo desafio para o processo de autenticação.</p>
     * 
     * @return objeto ChallengeResponse contendo o nonce e timestamp de expiração
     */
    public ChallengeResponse getChallenge() {
        return challengeService.generateChallenge();
    }

    /**
     * <p>Autentica um usuário com base na resposta ao desafio.</p>
     * 
     * @param request objeto contendo o nonce, assinatura e deviceId
     * @return token JWT para autenticação subsequente
     * @throws BusinessException se o desafio for inválido ou a assinatura for incorreta
     */
    public String authenticate(AuthenticationRequest request) {
        UUID nonce = request.getNonce();
        String signature = request.getSignature();
        String deviceId = request.getDeviceId();
        
        // Valida se o desafio existe e não está expirado
        if (!challengeService.validateChallenge(nonce)) {
            throw new BusinessException("Invalid or expired challenge");
        }
        
        // Valida a assinatura HMAC
        if (!hmacService.validateSignature(nonce, signature, deviceId)) {
            throw new BusinessException("Invalid signature");
        }
        
        // Remove o desafio após uso bem-sucedido
        challengeService.removeChallenge(nonce);
        
        // Cria um objeto Authentication com o deviceId como principal
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                deviceId,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        // Gera o token JWT
        return jwtService.generateToken(authentication);
    }

    /**
     * <p>Método legado para compatibilidade.</p>
     * 
     * @param authentication objeto Authentication do Spring Security
     * @return token JWT
     * @deprecated Use {@link #authenticate(AuthenticationRequest)} instead
     */
    @Deprecated
    public String authenticate(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }
}
