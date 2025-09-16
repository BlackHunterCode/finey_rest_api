package br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialSummary {
    private IncomeExpenseData income;
    private IncomeExpenseData expenses;
    private InvestmentData investments;
    private WalletBalance walletBalance;
    private ReturnRate totalReturnRate;
    private List<String> months;
}
