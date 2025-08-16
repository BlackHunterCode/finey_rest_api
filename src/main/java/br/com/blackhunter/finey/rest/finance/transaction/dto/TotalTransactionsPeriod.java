package br.com.blackhunter.finey.rest.finance.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalTransactionsPeriod {
    private BigDecimal totalEarnings;
    private BigDecimal totalExpenses;
}
