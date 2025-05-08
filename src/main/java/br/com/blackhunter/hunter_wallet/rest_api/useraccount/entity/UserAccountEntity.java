/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de conta de usuário.
 * Essa é a entidade base do sistema.
 * */
@Entity
@Table(name = "hw_useraccounts")
@Data
public class UserAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountId;
    private String accountName;
    @Column(unique = true)
    private String email;
    private String hashedPassword;
    private String accountUsername;
    private boolean accountIsActive;
    private LocalDateTime createdAt;

    /**
     * Construtor padrão da classe.
     * */
    public UserAccountEntity() {

    }
}
