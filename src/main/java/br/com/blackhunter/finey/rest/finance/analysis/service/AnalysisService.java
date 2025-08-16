package br.com.blackhunter.finey.rest.finance.analysis.service;

import br.com.blackhunter.finey.rest.auth.util.CryptUtil;
import br.com.blackhunter.finey.rest.auth.util.JwtUtil;
import br.com.blackhunter.finey.rest.core.dto.TransactionPeriodDate;
import br.com.blackhunter.finey.rest.core.util.DateTimeUtil;
import br.com.blackhunter.finey.rest.finance.analysis.dto.budget.BudgetReality;
import br.com.blackhunter.finey.rest.finance.analysis.dto.current_balance_projection.CurrentBalanceProjection;
import br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories.ExpensesCategories;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.FinancialSummary;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.IncomeExpenseData;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.InvestmentData;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.WalletBalance;
import br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary.ReturnRate;
import br.com.blackhunter.finey.rest.finance.analysis.dto.income.IncomeBreakdown;
import br.com.blackhunter.finey.rest.finance.analysis.dto.insights.Insights;
import br.com.blackhunter.finey.rest.finance.analysis.dto.investments.SavingsInvestments;
import br.com.blackhunter.finey.rest.finance.calc.service.FinancialSummaryCalcService;
import br.com.blackhunter.finey.rest.finance.calc.service.ExpensesCategoriesCalcService;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.FinancialIntegratorManager;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.dto.FinancialInstitutionData;
import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyAccountIds;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;

import br.com.blackhunter.finey.rest.finance.calc.service.BalanceProjectionCalcService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    @Value("${hunter.secrets.pluggy.crypt-secret}")
    private String PLUGGY_CRYPT_SECRET;

    private final JwtUtil jwtUtil;
    private final FinancialIntegratorManager financialIntegratorManager;
    private final FinancialSummaryCalcService financialSummaryCalcService;
    private final BalanceProjectionCalcService balanceProjectionCalcService;
    private final ExpensesCategoriesCalcService expensesCategoriesCalcService;

    public AnalysisService(
            JwtUtil jwtUtil,
            FinancialIntegratorManager financialIntegratorManager,
            FinancialSummaryCalcService financialSummaryCalcService,
            BalanceProjectionCalcService balanceProjectionCalcService,
            ExpensesCategoriesCalcService expensesCategoriesCalcService
    ) {
        this.jwtUtil = jwtUtil;
        this.financialIntegratorManager = financialIntegratorManager;
        this.financialSummaryCalcService = financialSummaryCalcService;
        this.balanceProjectionCalcService = balanceProjectionCalcService;
        this.expensesCategoriesCalcService = expensesCategoriesCalcService;
    }
    
    /**
     * Esse método é responsável por realizar a análise financeira e retornar um resumo financeiro.
     * Atenção: Todos os dados retornados por esse método estão criptografados.
     * 
     * @param bankAccountIds Lista de IDs de contas bancárias criptografadas
     * @param referenceDateMonthYear Data de referência (opcional, pode ser usado para filtros adicionais)
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return FinancialSummary com dados criptografados
     * */
    public FinancialSummary getFinancialSummaryAnalysisEncrypted(List<String> bankAccountIds, TransactionPeriodDate periodDate) {
        try {
            // Obter o usuário atual do JWT
            UserAccountEntity currentUser = jwtUtil.getUserAccountFromToken();
            UUID userId = currentUser.getAccountId();
   
            // Obter integrador financeiro
            FinancialIntegrator financialIntegrator = financialIntegratorManager.getFinancialIntegrator();

            // Lista de IDs criptografados fornecida pelo usuário
            List<String> encryptedBankAccountIds = bankAccountIds;

            // Descriptografar todos os IDs da lista do usuário
            List<String> decryptedUserBankAccountIds = new ArrayList<>();
            for (String encryptedId : encryptedBankAccountIds) {
                try {
                    String decryptedId = CryptUtil.decrypt(encryptedId, PLUGGY_CRYPT_SECRET);
                    decryptedUserBankAccountIds.add(decryptedId);
                } catch (Exception e) {
                    System.err.println("Erro ao descriptografar ID: " + encryptedId);
                }
            }

            // Filtrar as instituições financeiras conectadas
            List<FinancialInstitutionData> filteredBanks = financialIntegrator.getAllConnectedBanks(userId).stream()
                .filter(bank -> shouldIncludeBank(bank, decryptedUserBankAccountIds))
                .collect(Collectors.toList());

            // Calcular dados de receitas e despesas
            IncomeExpenseData income = financialSummaryCalcService.calculateIncomeDataEncrypted(financialIntegratorManager, bankAccountIds, periodDate);
            IncomeExpenseData expenses = financialSummaryCalcService.calculateExpenseDataEncrypted(financialIntegratorManager, bankAccountIds, periodDate);
            
            // Calcular dados de investimentos
            InvestmentData investments = financialSummaryCalcService.calculateInvestmentDataEncrypted(bankAccountIds, periodDate);
            
            // Calcular saldo da carteira
            WalletBalance walletBalance = financialSummaryCalcService.calculateWalletBalanceEncrypted(filteredBanks);
            
            // Calcular taxa de retorno total
            ReturnRate totalReturnRate = financialSummaryCalcService.calculateTotalReturnRateEncrypted(income, expenses, investments);
            
            // Criar lista de meses (pode ser expandida conforme necessário)
            List<String> months = financialSummaryCalcService.generateMonthsList(periodDate);
            
            return new FinancialSummary(
                income,
                expenses,
                investments,
                walletBalance,
                totalReturnRate,
                months
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing financial calculation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Realiza análise financeira e retorna projeção de saldo baseada em histórico de 3 meses.
     * Utiliza algoritmos estatísticos para projeções precisas de receitas e despesas.
     * 
     * <p><strong>Funcionalidades:</strong></p>
     * <ul>
     *   <li>Análise de padrões financeiros dos últimos 3 meses</li>
     *   <li>Cálculo de médias diárias de receitas e despesas</li>
     *   <li>Projeção de saldo para o final do mês atual</li>
     *   <li>Estimativa de gastos baseada em comportamento histórico</li>
     * </ul>
     * 
     * <p><strong>Dados retornados (criptografados):</strong></p>
     * <ul>
     *   <li><strong>currentBalance:</strong> Saldo atual de todas as contas</li>
     *   <li><strong>projectedBalance:</strong> Saldo projetado para fim do mês</li>
     *   <li><strong>daysLeftInMonth:</strong> Dias restantes no mês atual</li>
     *   <li><strong>dailyAverageExpense:</strong> Média diária de gastos (3 meses)</li>
     *   <li><strong>projectedSpending:</strong> Gastos estimados até fim do mês</li>
     * </ul>
     * 
     * @return projeção completa de saldo com dados criptografados
     * @throws RuntimeException se houver erro no cálculo da projeção
     */
    public CurrentBalanceProjection getCurrentBalanceProjectionEncrypted(List<String> bankAccountIds) {
        try {
            // Obter dados do usuário autenticado
            UserAccountEntity userAccount = jwtUtil.getUserAccountFromToken();
            UUID userId = userAccount.getAccountId( );
            
            // Obter bancos conectados e contas do usuário
            FinancialIntegrator financialIntegrator = financialIntegratorManager.getFinancialIntegrator();

                // Lista de IDs criptografados fornecida pelo usuário
            List<String> encryptedBankAccountIds = bankAccountIds;

            // Descriptografar todos os IDs da lista do usuário
            List<String> decryptedUserBankAccountIds = new ArrayList<>();
            for (String encryptedId : encryptedBankAccountIds) {
                try {
                    String decryptedId = CryptUtil.decrypt(encryptedId, PLUGGY_CRYPT_SECRET);
                    decryptedUserBankAccountIds.add(decryptedId);
                } catch (Exception e) {
                    System.err.println("Erro ao descriptografar ID: " + encryptedId);
                }
            }

            // Filtrar as instituições financeiras conectadas
            List<FinancialInstitutionData> filteredBanks = financialIntegrator.getAllConnectedBanks(userId).stream()
                .filter(bank -> shouldIncludeBank(bank, decryptedUserBankAccountIds))
                .collect(Collectors.toList());

            // Calcular projeção usando o serviço especializado
            return balanceProjectionCalcService.calculateBalanceProjectionEncrypted(
                financialIntegratorManager,
                filteredBanks,
                bankAccountIds
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error calculating balance projection: " + e.getMessage(), e);
        }
    }

    /**
     * Realiza análise financeira e retorna as categorias de despesas do usuário.
     * 
     * <p>Este método utiliza o <code>ExpensesCategoriesCalcService</code> para categorizar
     * automaticamente as despesas baseadas nas descrições das transações, calculando
     * valores totais, percentuais de participação e variações em relação ao período anterior.</p>
     * 
     * <p><strong>Funcionalidades:</strong></p>
     * <ul>
     *   <li>Categorização automática baseada em palavras-chave</li>
     *   <li>Cálculo de percentuais de participação por categoria</li>
     *   <li>Análise de variação temporal</li>
     *   <li>Ordenação por valor (maior para menor)</li>
     * </ul>
     * 
     * <p><strong>Categorias Suportadas:</strong></p>
     * <ul>
     *   <li>Alimentação (🍽️)</li>
     *   <li>Transporte (🚗)</li>
     *   <li>Moradia (🏠)</li>
     *   <li>Saúde (⚕️)</li>
     *   <li>Educação (📚)</li>
     *   <li>Lazer (🎬)</li>
     *   <li>Vestuário (👕)</li>
     *   <li>Outros (📊)</li>
     * </ul>
     * 
     * <p><strong>Atenção:</strong> Todos os dados retornados estão criptografados para segurança.</p>
     * 
     * @param bankAccountIds lista de IDs das contas bancárias (criptografados)
     * @param periodDate período de análise com data de início e fim
     * @return categorias de despesas com valores, percentuais e variações criptografados
     * @throws RuntimeException se houver erro no cálculo ou criptografia dos dados
     */
    public ExpensesCategories getExpensesCategoriesEncrypted(List<String> bankAccountIds, TransactionPeriodDate periodDate) {
        try {
            return expensesCategoriesCalcService.calculateExpensesCategoriesEncrypted(
                financialIntegratorManager,
                bankAccountIds,
                periodDate
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular categorias de despesas: " + e.getMessage(), e);
        }
    }

    /**
     * Esse método é responsável por realizar a análise financeira e retornar a realidade orçamentária.
     * Atenção: Todos os dados retornados por esse método estão criptografados.
     * */
    public BudgetReality getBudgetRealityEncrypted() {
        return new BudgetReality();
    }

    /**
     * Esse método é responsável por realizar a análise financeira e retornar os insights gerados pela IA.
     * Atenção: Todos os dados retornados por esse método estão criptografados.
     *
     * FUNCIONALIDADE PREMIUM.
     * */
    public Insights getInsightsEncrypted() {
        return new Insights();
    }

    /**
     * Esse método é responsável por realizar a análise financeira e retornar o resumo da renda.
     * Atenção: Todos os dados retornados por esse método estão criptografados.
     * */
    public IncomeBreakdown getIncomeBreakdownEncrypted() {
        return new IncomeBreakdown();
    }

    /**
     * Esse método é responsável por realizar a análise financeira e retornar os investimentos e economias.
     * Atenção: Todos os dados retornados por esse método estão criptografados.
     * */
    public SavingsInvestments getSavingsInvestmentsEncrypted() {
        return new SavingsInvestments();
    }


    /* Métodos privados */
    private boolean shouldIncludeBank(FinancialInstitutionData bank, List<String> decryptedUserBankAccountIds) {
    try {
        String encryptedAccountId = bank.getAccounts().stream()
            .map(PluggyAccountIds::getAccountId)
            .findFirst()
            .orElse(null);
        
        if (encryptedAccountId == null) {
            return false;
        }
        
        String decryptedAccountId = CryptUtil.decrypt(encryptedAccountId, PLUGGY_CRYPT_SECRET);
        return decryptedUserBankAccountIds.contains(decryptedAccountId);
    } catch (Exception e) {
        System.err.println("Error decrypting accountId: " + e.getMessage());
        return false;
    }
}
}
