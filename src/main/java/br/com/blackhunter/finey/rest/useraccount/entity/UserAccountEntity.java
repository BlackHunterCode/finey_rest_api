/*
 * @(#)UserAccountEntity.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.useraccount.entity;

import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyItemEntity;
import br.com.blackhunter.finey.rest.useraccount.enums.UserAccountStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * <p>Classe <code>UserAccountEntity</code>.</p>
 * <p>Entidade de conta de usuário.</p>
 * <p>Essa é a entidade base do sistema.</p>
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

    private String passwordHash;

    private String accountUsername;

    @Enumerated(EnumType.STRING)
    private UserAccountStatus accountStatus;

    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime endDateTimeOfTutorialPeriod;
    private LocalDateTime lastLoginAt;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private UserProfileEntity userProfile;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private Set<TransactionEntity> transactions;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private Set<PluggyItemEntity> pluggyItems;

    /**
     * Construtor padrão da classe.
     * */
    public UserAccountEntity() {

    }
}
