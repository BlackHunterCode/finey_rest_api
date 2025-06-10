/*
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 * Classe protegida - Alterações somente por CODEOWNERS.
 */
package br.com.blackhunter.hunter_wallet.rest_api.auth.service;

import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.AuthenticationRequest;
import br.com.blackhunter.hunter_wallet.rest_api.auth.dto.ChallengeResponse;
import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.enums.UserAccountStatus;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository.UserAccountRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * <p>Construtor para <code>AuthenticationService</code>.</p>
     * 
     * @param jwtService serviço para geração de tokens JWT
     * @param challengeService serviço para gerenciamento de desafios
     * @param hmacService serviço para validação de assinaturas HMAC
     * @param userAccountRepository repositório de contas de usuário
     * @param passwordEncoder codificador de senhas
     */
    public AuthenticationService(JwtService jwtService, 
                                 ChallengeService challengeService, 
                                 HmacService hmacService,
                                 UserAccountRepository userAccountRepository,
                                 PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.challengeService = challengeService;
        this.hmacService = hmacService;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
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
     * <p>Autentica um usuário com base na resposta ao desafio e nas credenciais do usuário.</p>
     * <p>O deviceId, nonce e a assinatura são obtidos dos headers da requisição.</p>
     * 
     * @param request objeto contendo email e password
     * @param deviceId identificador do dispositivo obtido do header X-Device-ID
     * @param signature assinatura HMAC obtida do header X-APP-Signature
     * @param nonceStr UUID do desafio obtido do header X-Nonce
     * @return token JWT para autenticação subsequente
     * @throws BusinessException se o desafio for inválido, a assinatura for incorreta ou as credenciais forem inválidas
     */
    public String authenticate(AuthenticationRequest request, String deviceId, String signature, String nonceStr) {
        UUID nonce;
        try {
            nonce = UUID.fromString(nonceStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid nonce format");
        }
        
        String email = request.getEmail();
        String password = request.getPassword();
        
        // Valida se o desafio existe e não está expirado
        if (!challengeService.validateChallenge(nonce)) {
            throw new BusinessException("Invalid or expired challenge");
        }
        
        // Valida a assinatura HMAC
        if (!hmacService.validateSignature(nonce, signature, deviceId)) {
            throw new BusinessException("Invalid signature");
        }
        
        // Valida as credenciais do usuário
        UserAccountEntity userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));
        
        // Verifica se a senha está correta
        if (!passwordEncoder.matches(password, userAccount.getPasswordHash())) {
            throw new BusinessException("Invalid email or password");
        }
        
        // Verifica se a conta está ativa
        if (userAccount.getAccountStatus() != UserAccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active");
        }
        
        // Remove o desafio após uso bem-sucedido
        challengeService.removeChallenge(nonce);
        
        // Cria um objeto Authentication com o email como principal
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email,
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
