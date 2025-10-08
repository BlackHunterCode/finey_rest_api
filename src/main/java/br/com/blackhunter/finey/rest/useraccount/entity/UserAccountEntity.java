/*
 * @(#)UserAccountEntity.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.useraccount.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyItemEntity;
import br.com.blackhunter.finey.rest.useraccount.enums.UserAccountStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * <p>Classe <code>UserAccountEntity</code>.</p>
 * <p>Entidade de conta de usuário.</p>
 * <p>Essa é a entidade base do sistema.</p>
 * */
@Entity
@Table(name = "fn_useraccounts")
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
    @ToString.Exclude
    private UserProfileEntity userProfile;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<TransactionEntity> transactions;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<PluggyItemEntity> pluggyItems;

    /**
     * Construtor padrão da classe.
     * */
    public UserAccountEntity() {

    }
}
