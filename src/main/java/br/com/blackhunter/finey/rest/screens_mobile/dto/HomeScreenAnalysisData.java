package br.com.blackhunter.finey.rest.screens_mobile.dto;

import br.com.blackhunter.finey.rest.finance.analysis.dto.budget.BudgetReality;
import br.com.blackhunter.finey.rest.finance.analysis.dto.current_balance_projection.CurrentBalanceProjection;
import br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories.ExpensesCategories;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.FinancialSummary;
import br.com.blackhunter.finey.rest.finance.analysis.dto.income.IncomeBreakdown;
import br.com.blackhunter.finey.rest.finance.analysis.dto.insights.Insights;
import br.com.blackhunter.finey.rest.finance.analysis.dto.investments.SavingsInvestments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeScreenAnalysisData {
    private String analysisPeriod;
    private FinancialSummary financialSummary;
    private CurrentBalanceProjection currentBalanceProjection;
    private ExpensesCategories expenseCategories;
    private BudgetReality budgetReality;
    private Insights aiInsights;
    private IncomeBreakdown incomeBreakdown;
    private SavingsInvestments savingsInvestments;
}
