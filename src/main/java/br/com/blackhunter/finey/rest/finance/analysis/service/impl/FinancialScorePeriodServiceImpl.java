package br.com.blackhunter.finey.rest.finance.analysis.service.impl;

import br.com.blackhunter.finey.rest.auth.util.CryptUtil;
import br.com.blackhunter.finey.rest.core.dto.TransactionPeriodDate;
import br.com.blackhunter.finey.rest.core.util.DateTimeUtil;
import br.com.blackhunter.finey.rest.core.util.Utils;
import br.com.blackhunter.finey.rest.finance.analysis.dto.score.FinancialScorePeriodDTO;
import br.com.blackhunter.finey.rest.finance.analysis.dto.insights.InsightDTO;
import br.com.blackhunter.finey.rest.finance.analysis.service.FinancialScorePeriodService;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.FinancialSummary;
import br.com.blackhunter.finey.rest.finance.analysis.service.HomeScreenAnalysisService;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe <code>FinancialScorePeriodServiceImpl</code> que implementa a interface <code>FinancialScorePeriodService</code>.
 * Essa classe é responsável por fornecer a implementação do serviço de cálculo do score financeiro para um período específico.
 * */
public class FinancialScorePeriodServiceImpl implements FinancialScorePeriodService {
    @Value("${hunter.secrets.pluggy.crypt-secret}")
    private String PLUGGY_CRYPT_SECRET;

    private final HomeScreenAnalysisService homeScreenAnalysisService;

    public FinancialScorePeriodServiceImpl(HomeScreenAnalysisService homeScreenAnalysisService) {
        this.homeScreenAnalysisService = homeScreenAnalysisService;
    }

    @Override
    public FinancialScorePeriodDTO getFinancialScorePeriod(List<String> bankAccountIds, LocalDate referenceDateMonthYear, LocalDate startDate, LocalDate endDate) {
        try {
            TransactionPeriodDate periodDate = DateTimeUtil.getTransactionPeriodDate(referenceDateMonthYear, startDate, endDate);

            FinancialSummary financialSummary = this.homeScreenAnalysisService.getFinancialSummaryAnalysisEncrypted(bankAccountIds, periodDate);

            Double score = calculateFinancialScore(financialSummary);
            Double percentage = calculatePercentage(periodDate, bankAccountIds, financialSummary);
            String details = getScoreDetails(score, percentage, periodDate);
            List<InsightDTO> insights = getInsights(financialSummary);

            return new FinancialScorePeriodDTO(
                    CryptUtil.encrypt(periodDate.getStartDate() + " - " + periodDate.getEndDate(), PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt(String.valueOf(score), PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt(String.valueOf(percentage), PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt(details, PLUGGY_CRYPT_SECRET),
                    insights
            );
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Métodos privados */

    /**
     * Cálculo do score financeiro baseado em receitas, despesas e investimentos.
     *
     * @param financialSummary O resumo financeiro do período.
     *
     * @return O score financeiro calculado, variando de 0 a 100.
     * */
    private Double calculateFinancialScore(FinancialSummary financialSummary) throws Exception {
        Double incomes     = Utils.safeParseDouble(CryptUtil.decrypt(financialSummary.getIncome().getValue(), PLUGGY_CRYPT_SECRET));
        Double expenses    = Utils.safeParseDouble(CryptUtil.decrypt(financialSummary.getExpenses().getValue(), PLUGGY_CRYPT_SECRET));
        Double investiments = Utils.safeParseDouble(CryptUtil.decrypt(financialSummary.getInvestments().getValue(), PLUGGY_CRYPT_SECRET));

        // Evitar divisão por zero
        if (incomes == 0.0) {
            return 0.0; // Score mínimo se não houve receita
        }

        // Cálculo dos percentuais
        double savingRate = (incomes - expenses) / incomes; // % economizado
        double investmentRate = investiments / incomes; // % investido

        // Score base: 60% pelo savingRate, 30% pelo investmentRate, 10% bônus se saldo positivo
        double score = 60 * Math.max(0, Math.min(1, savingRate)) +
                30 * Math.max(0, Math.min(1, investmentRate));

        // Bônus se não gastou mais do que recebeu
        if (incomes - expenses > 0) {
            score += 10;
        }

        // Limitar score entre 0 e 100
        return Math.max(0, Math.min(100, score));
    }

    /**
     * O cálculo do percentage é sempre feito com base no mês anterior ao período analisado.
     *
     * @param periodDate O período atual de análise.
     * @param bankAccountIds Id's das contas bancárias que serão analisadas.
     * @param financialSummary O resumo financeiro do período atual.
     *
     * @return A variação percentual da receita em relação ao período anterior, ou null se não houver dados suficientes.
     * */
    private Double calculatePercentage(TransactionPeriodDate periodDate, List<String> bankAccountIds, FinancialSummary financialSummary) {
        try {
            // 1. Definir período do mês anterior
            LocalDate previousStart = periodDate.getStartDate().minusMonths(1).withDayOfMonth(1);
            LocalDate previousEnd = previousStart.withDayOfMonth(previousStart.lengthOfMonth());
            TransactionPeriodDate previousPeriod = new TransactionPeriodDate(previousStart, previousEnd);

            // 2. Buscar FinancialSummary do mês anterior
            FinancialSummary previousSummary = this.homeScreenAnalysisService.getFinancialSummaryAnalysisEncrypted(bankAccountIds, previousPeriod);

            if (previousSummary == null) return 0.0;

            // 3. Obter valores de receita do período atual e anterior
            Double currentIncome = Utils.safeParseDouble(CryptUtil.decrypt(financialSummary.getIncome().getValue(), PLUGGY_CRYPT_SECRET));
            Double previousIncome = Utils.safeParseDouble(CryptUtil.decrypt(previousSummary.getIncome().getValue(), PLUGGY_CRYPT_SECRET));

            if (previousIncome == 0.0) return null; // Evita divisão por zero

            // 4. Calcular variação percentual
            return ((currentIncome - previousIncome) / previousIncome) * 100.0;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Formata os detalhes do score financeiro para exibição.
     * @param score O score financeiro calculado.
     * @param percentage A variação percentual da receita em relação ao período anterior.
     * @param periodDate O período de transação analisado.
     *
     * @return Detalhes formatado
     * */
    private String getScoreDetails(Double score, Double percentage, TransactionPeriodDate periodDate) {
        StringBuilder details = new StringBuilder();
        details.append(String.format("Score financeiro para o período de %s até %s:\n", periodDate.getStartDate(), periodDate.getEndDate()));
        details.append(String.format("- Score: %.2f de 100\n", score != null ? score : 0.0));
        if (percentage != null) {
            details.append(String.format("- Variação da renda em relação ao período anterior: %.2f%%\n", percentage));
        } else {
            details.append("- Variação da renda em relação ao período anterior: N/A (sem dados do período anterior)\n");
        }
        return details.toString();
    }

    /***
     * Gera insights financeiros baseados no resumo financeiro.
     *
     * @param financialSummary O resumo financeiro do período.
     *
     * @return Uma lista de insights financeiros relevantes.
     * */
    private List<InsightDTO> getInsights(FinancialSummary financialSummary) throws Exception {
        List<InsightDTO> insights = new ArrayList<>();
        Double incomes            = Utils.safeParseDouble(CryptUtil.decrypt(financialSummary.getIncome().getValue(), PLUGGY_CRYPT_SECRET));
        Double expenses           = Utils.safeParseDouble(CryptUtil.decrypt(financialSummary.getExpenses().getValue(), PLUGGY_CRYPT_SECRET));
        boolean metaAtingida      = false; // Suponha que isso venha de algum lugar

        if (incomes > expenses) {
            insights.add(new InsightDTO(
                    CryptUtil.encrypt("Atenção aos gastos!", PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt("Seus gastos este mês estão acima da média dos últimos meses.", PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt("⚠️", PLUGGY_CRYPT_SECRET),
                    false,
                    null,
                    null
            ));
        }

        if (!metaAtingida) {
            insights.add(new InsightDTO(
                    CryptUtil.encrypt("Meta de poupança não atingida", PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt("Você não atingiu sua meta de poupança este mês. Reveja seus gastos.", PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt("💸", PLUGGY_CRYPT_SECRET),
                    true,
                    CryptUtil.encrypt("Ver dicas para economizar", PLUGGY_CRYPT_SECRET),
                    CryptUtil.encrypt("https://app.finey.com.br/dicas-economia", PLUGGY_CRYPT_SECRET)
            ));
        }

        return insights;
    }
}
