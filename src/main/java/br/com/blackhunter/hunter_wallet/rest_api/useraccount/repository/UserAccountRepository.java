/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository;

import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
