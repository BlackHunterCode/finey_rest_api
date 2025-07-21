/*
 * @(#)TransactionEntity.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.finance.transaction.entity;

import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionStatus;
import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionType;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hw_transactions")
@Data
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID transactionId;
    @Column(name = "open_banking_account_id", nullable = false, updatable = false)
    private UUID openBankingAccountId;

    @ManyToOne
    @JoinColumn(name = "user_account_id", updatable = false)
    private UserAccountEntity userAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Lob
    @Column(nullable = false)
    private String description;
    @Lob
    @Column(name = "description_raw")
    private String descriptionRaw;

    @Column(nullable = false)
    private BigDecimal balance;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    private String category;

    @Column(name = "provider_transaction_code")
    private String providerTransactionCode;
    @Column(name = "provider_transaction_id")
    private String providerTransactionId;

    @Column(name = "provider_transaction_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type; // o que define se é um crédito (ganho) ou débito (gasto)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
