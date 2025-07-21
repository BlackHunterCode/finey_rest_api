/*
 * @(#)UserAccountRepository.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository;

import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.projections.UserInfoData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * <p>Interface <code>UserAccountRepository</code>.</p>
 * <p>Interface Repositório de conta de usuário, muito usado para consultar, salvar, atualizar e deletar
 * entidades no banco de dados.</p>
 *
 * <p>Extends: {@link JpaRepository}</p>
 * */
public interface UserAccountRepository extends JpaRepository<UserAccountEntity, UUID> {
    /**
     * @param email E-mail a ser verificado
     * <p>Verifica se o email informado já está cadastrado no banco de dados.</p>
     *
     * @return <code>true</code> se estiver cadastrado ou <code>false</code> se não estiver.
     * */
    @Query("SELECT COUNT(*) > 0 FROM UserAccountEntity u WHERE u.email = :email")
    boolean existsByEmail(@Param(value = "email") String email);

    Optional<UserAccountEntity> findByEmail(String email);
    
    /**
     * @param email E-mail do usuário a ser excluído
     * <p>Remove a conta de usuário com o e-mail especificado.</p>
     * */
    void deleteByEmail(String email);

    /**
     * @param email e-mail do usuário a ser verificado
     * <p>Busca o ID do usuário associado ao e-mail fornecido.</p>
     *
     * @return ID do usuário se existir, ou <code>null</code> se não existir.
     */
    @Query("SELECT u.accountId FROM UserAccountEntity u WHERE u.email = :email")
    Optional<UUID> findUserIdByEmail(@Param(value = "email") String email);

    /**
     * @param userId ID do usuário a ser buscado
     * <p>Busca informações gerais do usuário.</p>
     *
     * @return Dados gerais do usuário.
     * */
    @Query(
            "SELECT ua.accountId as accountId,                                          " +
            "       CONCAT(up.firstName, ' ', up.lastName) as fullName,                 " +
            "       up.firstName as firstName, up.lastName as lastName,                 " +
            "       ua.endDateTimeOfTutorialPeriod as endDateTimeOfTutorialPeriod,      " +
            "       ua.accountStatus as accountStatus                                   " +
            "FROM UserAccountEntity ua                                                  " +
            "INNER JOIN UserProfileEntity up ON ua.accountId = up.userAccount.accountId " +
            "WHERE ua.accountId = :userId"
    )
    UserInfoData getUserInfoById(@Param("userId") UUID userId);
}
