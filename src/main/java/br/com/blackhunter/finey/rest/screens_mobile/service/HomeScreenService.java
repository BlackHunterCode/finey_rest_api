package br.com.blackhunter.finey.rest.screens_mobile.service;

import br.com.blackhunter.finey.rest.core.dto.TransactionPeriodDate;
import br.com.blackhunter.finey.rest.core.util.DateTimeUtil;
import br.com.blackhunter.finey.rest.finance.analysis.dto.budget.BudgetReality;
import br.com.blackhunter.finey.rest.finance.analysis.dto.current_balance_projection.CurrentBalanceProjection;
import br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories.ExpensesCategories;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.FinancialSummary;
import br.com.blackhunter.finey.rest.finance.analysis.dto.income.IncomeBreakdown;
import br.com.blackhunter.finey.rest.finance.analysis.dto.insights.Insights;
import br.com.blackhunter.finey.rest.finance.analysis.dto.investments.SavingsInvestments;
import br.com.blackhunter.finey.rest.finance.analysis.service.AnalysisService;
import br.com.blackhunter.finey.rest.screens_mobile.dto.HomeScreenAnalysisData;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HomeScreenService {
    private AnalysisService analysisService;

    public HomeScreenService(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    public HomeScreenAnalysisData getHomeScreenAnalysisFromReferenceDate(List<String> bankAccountIds, LocalDate referenceDate, LocalDate startDate, LocalDate endDate) {
        TransactionPeriodDate periodDate = DateTimeUtil.getTransactionPeriodDate(referenceDate, startDate, endDate);

        FinancialSummary financialSummary = analysisService.getFinancialSummaryAnalysisEncrypted(bankAccountIds, periodDate);
        CurrentBalanceProjection currentBalanceProjection = analysisService.getCurrentBalanceProjectionEncrypted(bankAccountIds);
        ExpensesCategories expenseCategories = analysisService.getExpensesCategoriesEncrypted(bankAccountIds, periodDate);
        BudgetReality budgetReality = analysisService.getBudgetRealityEncrypted();
        Insights aiInsights = analysisService.getInsightsEncrypted();
        IncomeBreakdown incomeBreakdown = analysisService.getIncomeBreakdownEncrypted(bankAccountIds, periodDate);
        SavingsInvestments savingsInvestments = analysisService.getSavingsInvestmentsEncrypted(bankAccountIds, periodDate);

        return new HomeScreenAnalysisData(
            periodDate.getStartDate() + " to " + periodDate.getEndDate(),
            financialSummary,
            currentBalanceProjection,
            expenseCategories,
            budgetReality,
            aiInsights,
            incomeBreakdown,
            savingsInvestments
        );
    }
}
