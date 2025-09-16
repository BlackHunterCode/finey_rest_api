package br.com.blackhunter.finey.rest.finance.analysis.dto.current_balance_projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentBalanceProjection {
    private String currentBalance;
    private String projectedBalance;
    private int daysLeftInMonth;
    private String dailyAverageExpense;
    private String projectedSpending;
}
