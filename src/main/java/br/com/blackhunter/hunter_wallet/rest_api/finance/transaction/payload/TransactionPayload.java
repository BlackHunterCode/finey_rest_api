/*
 * @(#)TransactionPayload.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.payload;

import br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPayload {
    private BigDecimal amount;
    private String description;
    private String descriptionRaw;
    private BigDecimal balance;
    private String currencyCode;
    private String category;
    private TransactionType type;
    private Boolean approved;
    private LocalDateTime transactionDate;

    public Boolean isApproved() {
        return approved;
    }
}
