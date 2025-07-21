/*
 * @(#)TransactionPayload.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.finance.transaction.payload;

import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPayload {
    @NotNull(message = "The amount field is mandatory.")
    private BigDecimal amount;
    @NotBlank(message = "The description field is mandatory.")
    private String description;
    private String descriptionRaw;
    @NotNull(message = "The balance field is mandatory.")
    private BigDecimal balance;
    @NotBlank(message = "The currency code field is mandatory.")
    private String currencyCode;
    private String category;
    @NotNull(message = "The type field is mandatory.")
    private TransactionType type;
    private Boolean approved;
    @NotNull(message = "The transaction date field is mandatory.")
    @PastOrPresent(message = "The transaction date must be in the past or present.")
    private LocalDateTime transactionDate;

    public Boolean isApproved() {
        return approved;
    }
}
