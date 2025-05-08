/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.service;

import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.UserAccountData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.mapper.UserAccountMapper;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.payload.UserAccountPayload;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository.UserAccountRepository;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.validation.UserAccountValidator;

import java.time.LocalDateTime;

/**
 * <p>Classe <code>UserAccountService</code>.</p>
 * <p>Classe de serviços da conta de usuário.</p>
 * */
public class UserAccountService {
    private final UserAccountValidator validator;
    private final UserAccountMapper mapper;
    private final UserAccountRepository repository;
    /**
     * Injeção de dependências:
     * @param validator Validador de situações complexas de contas de usuários
     * @param mapper Mapeador de classes de contas de usuários
     * @param repository Repositório de dados de contas de usuários
     * */
    public UserAccountService(
            UserAccountValidator validator,
            UserAccountMapper mapper,
            UserAccountRepository repository) {
        this.validator = validator;
        this.mapper = mapper;
        this.repository = repository;
    }

    /**
     * @param reqPayload
     * <p>Método que irá registar uma conta de usuário no banco dados.</p>
     *
     * @return usuário criado;
     * */
    public UserAccountData createUserAccount(UserAccountPayload reqPayload) {
        /*
        * É importante considerar que a classe UserAccountPayload já valida
        * possíveis valores nulos
        *  */
        validator.validateEmailUniqueness(reqPayload.getEmail());
        UserAccountEntity entity = mapper.toEntity(reqPayload);
        entity.setAccountIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        return mapper.toData(repository.saveAndFlush(entity));
    }
}
