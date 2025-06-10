/*
 * @(#)TransactionData.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.dto;

import br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.enums.TransactionStatus;
import br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionData {
    private UUID transactionId;
    private BigDecimal amount;
    private String description;
    private String descriptionRaw;
    private BigDecimal balance;
    private String currencyCode;
    private String category;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime transactionDate;
}
