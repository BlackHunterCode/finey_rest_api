/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.impl;

import br.com.blackhunter.hunter_wallet.rest_api.auth.util.JwtUtil;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegratorManager;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.UserAccountData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.projections.UserInfoData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.projections.UserInfoDataProjected;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.enums.UserAccountStatus;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.mapper.UserAccountMapper;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.payload.UserAccountPayload;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository.UserAccountRepository;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.UserAccountService;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.validation.UserAccountValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <p>Classe <code>UserAccountService</code>.</p>
 * <p>Classe de serviços da conta de usuário.</p>
 * */
@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountValidator validator;
    private final UserAccountMapper mapper;
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private JwtUtil jwtUtil;

    @Lazy
    @Autowired
    private FinancialIntegratorManager financialIntegratorManager;

    /**
     * Injeção de dependências:
     * @param validator Validador de situações complexas de contas de usuários
     * @param mapper Mapeador de classes de contas de usuários
     * @param repository Repositório de dados de contas de usuários
     * @param passwordEncoder Codificador de senhas
     * */
    public UserAccountServiceImpl(
            UserAccountValidator validator,
            UserAccountMapper mapper,
            UserAccountRepository repository,
            PasswordEncoder passwordEncoder){
        this.validator = validator;
        this.mapper = mapper;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param reqPayload
     * <p>Método que irá registar uma conta de usuário no banco dados.</p>
     *
     * @return usuário criado;
     * */
    @Override
    public UserAccountData registerUser(UserAccountPayload reqPayload) {
        validator.validateEmailUniqueness(reqPayload.getEmail());
        UserAccountEntity entity = mapper.toEntity(reqPayload);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setAccountStatus(UserAccountStatus.ACTIVE);
        // por enquanto nao existe validação para garantir que a senha ja vem criptografada
        // entao, vamos criptografar a senha aqui fixa por enquanto
        entity.setPasswordHash(passwordEncoder.encode(reqPayload.getHashedPassword()));
        entity.setEndDateTimeOfTutorialPeriod(entity.getCreatedAt().plusDays(3)); // 3 dias de tutorial
        return mapper.toData(repository.save(entity));
    }

    @Override
    public UserAccountData authenticateUser(Object loginDTO) {
        // Fazer depois..
        return null;
    }

    @Override
    public UserInfoDataProjected getUserInfoByAuthToken() {
        UserAccountEntity user = jwtUtil.getUserAccountFromToken();

        FinancialIntegrator financialIntegrator = financialIntegratorManager.getFinancialIntegrator();
        List<String> userConnectedBanks = financialIntegrator.getAllConnectedBanks(user.getAccountId());
        return new UserInfoDataProjected(repository.getUserInfoById(user.getAccountId()), userConnectedBanks);
    }

    @Override
    public UserAccountData findById(UUID userId) {
        return this.mapper.toData(findEntityById(userId));
    }

    @Override
    public UserAccountEntity findEntityById(UUID userId) {
        return this.repository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("The user account provided does not exist.")
        );
    }

    @Override
    public UUID findUserIdByEmail(String email) {
        return repository.findUserIdByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("The user account with the provided email does not exist."));
    }

    @Override
    public void verifyEmail(String verificationToken) {
        // Fazer...
    }

    @Override
    public void initiatePasswordReset(String email) {
        // Fazer...
    }

    @Override
    public void completePasswordReset(String token, String newPassword) {
        // Fazer...
    }

    @Override
    public void deactivateAccount(UUID userId) {
        // Fazer...
    }

    private boolean isAccountExists(UUID userId) {
        return repository.existsById(userId);
    }
}
