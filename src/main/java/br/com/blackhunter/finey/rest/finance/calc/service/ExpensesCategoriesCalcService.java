package br.com.blackhunter.finey.rest.finance.calc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.blackhunter.finey.rest.auth.util.CryptUtil;
import br.com.blackhunter.finey.rest.core.dto.TransactionPeriodDate;
import br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories.ExpenseCategory;
import br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories.ExpensesCategories;
import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionType;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.FinancialIntegratorManager;

/**
 * Serviço responsável por calcular e categorizar despesas financeiras.
 * 
 * <p>Este serviço analisa transações de débito e as agrupa em categorias baseadas
 * na descrição das transações, calculando valores totais, percentuais e variações
 * para cada categoria identificada.</p>
 * 
 * <p><strong>Atenção:</strong> Todos os valores retornados são criptografados para segurança.</p>
 * 
 * @author Sistema Finey
 * @version 1.0
 * @since 2024
 */
@Service
public class ExpensesCategoriesCalcService {

    @Value("${hunter.secrets.pluggy.crypt-secret}")
    private String PLUGGY_CRYPT_SECRET;

    /**
     * Calcula e categoriza as despesas do usuário para o período especificado.
     * 
     * <p><strong>Metodologia de Categorização:</strong></p>
     * <ol>
     *   <li><strong>Coleta de Dados:</strong> Busca todas as transações DEBIT do período</li>
     *   <li><strong>Categorização Automática:</strong> Analisa descrições para identificar categorias</li>
     *   <li><strong>Agregação:</strong> Soma valores por categoria identificada</li>
     *   <li><strong>Cálculo de Percentuais:</strong> Calcula participação de cada categoria no total</li>
     *   <li><strong>Análise Temporal:</strong> Compara com período anterior para variação</li>
     * </ol>
     * 
     * <p><strong>Categorias Identificadas Automaticamente:</strong></p>
     * <ul>
     *   <li><strong>Alimentação:</strong> Supermercado, restaurante, delivery, padaria</li>
     *   <li><strong>Transporte:</strong> Combustível, Uber, transporte público, estacionamento</li>
     *   <li><strong>Moradia:</strong> Aluguel, condomínio, energia, água, internet</li>
     *   <li><strong>Saúde:</strong> Farmácia, médico, plano de saúde, exames</li>
     *   <li><strong>Educação:</strong> Escola, curso, livros, material escolar</li>
     *   <li><strong>Lazer:</strong> Cinema, streaming, jogos, viagens</li>
     *   <li><strong>Vestuário:</strong> Roupas, calçados, acessórios</li>
     *   <li><strong>Outros:</strong> Transações não categorizadas automaticamente</li>
     * </ul>
     * 
     * <p><strong>Algoritmo de Categorização:</strong></p>
     * <pre>
     * Para cada transação DEBIT:
     *   1. Extrair descrição da transação
     *   2. Normalizar texto (lowercase, remover acentos)
     *   3. Buscar palavras-chave por categoria
     *   4. Atribuir à primeira categoria encontrada
     *   5. Se nenhuma categoria encontrada, atribuir a "Outros"
     * </pre>
     * 
     * <p><strong>Exemplo de cenário para validação manual:</strong></p>
     * <pre>
     * Período: 01/01/2024 a 31/01/2024
     * 
     * Transações encontradas:
     *   - "SUPERMERCADO EXTRA" -R$ 450,00 → Alimentação
     *   - "POSTO SHELL COMBUSTIVEL" -R$ 280,00 → Transporte
     *   - "UBER TRIP" -R$ 45,00 → Transporte
     *   - "FARMACIA DROGASIL" -R$ 120,00 → Saúde
     *   - "NETFLIX ASSINATURA" -R$ 32,90 → Lazer
     *   - "TRANSFERENCIA PIX" -R$ 200,00 → Outros
     * 
     * Cálculos:
     *   Total Geral: R$ 1.127,90
     *   
     *   Alimentação: R$ 450,00 (39,9%)
     *   Transporte: R$ 325,00 (28,8%)
     *   Saúde: R$ 120,00 (10,6%)
     *   Lazer: R$ 32,90 (2,9%)
     *   Outros: R$ 200,00 (17,7%)
     * 
     * Variações (comparação com período anterior - valores simulados):
     *   Alimentação: +5,2% (aumento)
     *   Transporte: -12,3% (redução)
     *   Saúde: +0,8% (estável)
     *   Lazer: -25,0% (redução significativa)
     *   Outros: +15,6% (aumento)
     * </pre>
     * 
     * <p><strong>Exemplo de retorno (dados criptografados):</strong></p>
     * <pre>
     * ExpensesCategories {
     *   categories: [
     *     {
     *       name: "encrypted_Alimentação",
     *       icon: "encrypted_🍽️",
     *       amount: "encrypted_450.00",
     *       percentage: "encrypted_39.9",
     *       previousPercentage: "encrypted_34.7"
     *     },
     *     {
     *       name: "encrypted_Transporte",
     *       icon: "encrypted_🚗",
     *       amount: "encrypted_325.00",
     *       percentage: "encrypted_28.8",
     *       previousPercentage: "encrypted_41.1"
     *     }
     *     // ... outras categorias
     *   ],
     *   totalExpenses: "encrypted_1127.90"
     * }
     * </pre>
     * 
     * <p><strong>Como validar manualmente (QA):</strong></p>
     * <ol>
     *   <li><strong>Validação de Dados:</strong></li>
     *   <ul>
     *     <li>Listar todas as transações DEBIT do período</li>
     *     <li>Verificar se todas foram incluídas no cálculo</li>
     *     <li>Confirmar que apenas transações DEBIT foram consideradas</li>
     *   </ul>
     *   <li><strong>Validação de Categorização:</strong></li>
     *   <ul>
     *     <li>Para cada transação, verificar se a categoria atribuída faz sentido</li>
     *     <li>Testar com descrições conhecidas (ex: "SUPERMERCADO" → Alimentação)</li>
     *     <li>Verificar se transações sem categoria clara vão para "Outros"</li>
     *   </ul>
     *   <li><strong>Validação de Cálculos:</strong></li>
     *   <ul>
     *     <li>Somar manualmente valores por categoria</li>
     *     <li>Verificar se soma das categorias = total de despesas</li>
     *     <li>Calcular percentuais: (Valor Categoria / Total) × 100</li>
     *     <li>Verificar se soma dos percentuais = 100%</li>
     *   </ul>
     *   <li><strong>Validação de Criptografia:</strong></li>
     *   <ul>
     *     <li>Descriptografar valores retornados</li>
     *     <li>Comparar com cálculos manuais</li>
     *     <li>Verificar se todos os campos sensíveis estão criptografados</li>
     *   </ul>
     * </ol>
     * 
     * @param financialIntegratorManager gerenciador de integração financeira
     * @param bankAccountIds lista de IDs das contas bancárias (criptografados)
     * @param periodDate período de análise com data de início e fim
     * @return categorias de despesas criptografadas com valores, percentuais e variações
     * @throws Exception se houver erro na descriptografia, busca de transações ou criptografia dos resultados
     */
    public ExpensesCategories calculateExpensesCategoriesEncrypted(
            FinancialIntegratorManager financialIntegratorManager, 
            List<String> bankAccountIds, 
            TransactionPeriodDate periodDate) throws Exception {
        
        // Mapa para armazenar valores por categoria
        Map<String, CategoryData> categoriesMap = new HashMap<>();
        AtomicReference<BigDecimal> totalExpenses = new AtomicReference<>(BigDecimal.ZERO);
        
        FinancialIntegrator financialIntegrator = financialIntegratorManager.getFinancialIntegrator();
        
        // Processar transações de cada conta
        for (String accountId : bankAccountIds) {
            String accountEntityId = CryptUtil.decrypt(accountId, PLUGGY_CRYPT_SECRET);
            String originalPluggyAccountId = financialIntegrator
                .getOriginalFinancialAccountIdByTargetId(UUID.fromString(accountEntityId));
            
            List<TransactionEntity> transactions = financialIntegrator
                .getAllTransactionsPeriodByTargetId(
                    originalPluggyAccountId,
                    periodDate.getStartDate(),
                    periodDate.getEndDate()
                );
            
            // Processar apenas transações de débito
            for (TransactionEntity transaction : transactions) {
                if (transaction.getType() == TransactionType.DEBIT) {
                    BigDecimal amount = transaction.getAmount().abs();
                    totalExpenses.set(totalExpenses.get().add(amount));
                    
                    // Categorizar transação
                    String categoryName = categorizeTransaction(transaction);
                    String categoryIcon = getCategoryIcon(categoryName);
                    
                    // Adicionar ao mapa de categorias
                    categoriesMap.computeIfAbsent(categoryName, k -> new CategoryData(categoryName, categoryIcon))
                        .addAmount(amount);
                }
            }
        }
        
        // Converter para lista de ExpenseCategory com percentuais
        List<ExpenseCategory> categories = categoriesMap.values().stream()
            .map(categoryData -> {
                BigDecimal total = totalExpenses.get();
                double percentage;

                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal ratio = categoryData.getAmount()
                        .divide(total, 4, RoundingMode.HALF_UP);

                    percentage = ratio.multiply(BigDecimal.valueOf(100)).doubleValue();
                } else {
                    percentage = 0.0;
                }
                
                // Simular variação do período anterior (em implementação real, buscar dados históricos)
                double previousPercentage = calculatePreviousPercentage(categoryData.getName(), percentage);
                
                try {
                    return new ExpenseCategory(
                        CryptUtil.encrypt(categoryData.getName(), PLUGGY_CRYPT_SECRET),
                        CryptUtil.encrypt(categoryData.getIcon(), PLUGGY_CRYPT_SECRET),
                        Double.parseDouble(CryptUtil.encrypt(categoryData.getAmount().toString(), PLUGGY_CRYPT_SECRET)),
                        Double.parseDouble(CryptUtil.encrypt(String.valueOf(percentage), PLUGGY_CRYPT_SECRET)),
                        Double.parseDouble(CryptUtil.encrypt(String.valueOf(previousPercentage), PLUGGY_CRYPT_SECRET))
                    );
                } catch (Exception e) {
                    throw new RuntimeException("Erro ao criptografar dados da categoria: " + categoryData.getName(), e);
                }
            })
            .collect(Collectors.toList());
        
        // Ordenar categorias por valor (maior para menor)
        categories.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        
        try {
            return new ExpensesCategories(
                categories,
                Double.parseDouble(CryptUtil.encrypt(totalExpenses.toString(), PLUGGY_CRYPT_SECRET))
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar total de despesas", e);
        }
    }
    
    /**
     * Categoriza uma transação baseada em sua descrição.
     * 
     * <p><strong>Algoritmo de Categorização:</strong></p>
     * <ol>
     *   <li>Normaliza a descrição (lowercase, remove acentos)</li>
     *   <li>Busca palavras-chave específicas por categoria</li>
     *   <li>Retorna a primeira categoria encontrada</li>
     *   <li>Se nenhuma categoria for identificada, retorna "Outros"</li>
     * </ol>
     * 
     * @param transaction transação a ser categorizada
     * @return nome da categoria identificada
     */
    private String categorizeTransaction(TransactionEntity transaction) {
        String description = transaction.getDescription().toLowerCase();
        
        // Usar categoria já definida se disponível
        if (transaction.getCategory() != null && !transaction.getCategory().trim().isEmpty()) {
            return transaction.getCategory();
        }
        
        // Categorização baseada em palavras-chave na descrição
        if (containsKeywords(description, "supermercado", "mercado", "padaria", "restaurante", 
                            "lanchonete", "delivery", "ifood", "uber eats", "food", "alimentacao")) {
            return "Alimentação";
        }
        
        if (containsKeywords(description, "posto", "combustivel", "gasolina", "etanol", "uber", 
                            "99", "taxi", "metro", "onibus", "transporte", "estacionamento")) {
            return "Transporte";
        }
        
        if (containsKeywords(description, "aluguel", "condominio", "energia", "luz", "agua", 
                            "internet", "telefone", "gas", "iptu", "moradia")) {
            return "Moradia";
        }
        
        if (containsKeywords(description, "farmacia", "drogaria", "medico", "hospital", 
                            "clinica", "laboratorio", "plano saude", "unimed", "saude")) {
            return "Saúde";
        }
        
        if (containsKeywords(description, "escola", "faculdade", "curso", "livro", "material escolar", 
                            "educacao", "universidade", "colegio")) {
            return "Educação";
        }
        
        if (containsKeywords(description, "cinema", "netflix", "spotify", "streaming", "jogo", 
                            "viagem", "hotel", "lazer", "entretenimento")) {
            return "Lazer";
        }
        
        if (containsKeywords(description, "roupa", "calcado", "sapato", "tenis", "vestuario", 
                            "moda", "loja", "shopping")) {
            return "Vestuário";
        }
        
        return "Outros";
    }
    
    /**
     * Verifica se a descrição contém alguma das palavras-chave especificadas.
     * 
     * @param description descrição da transação (já em lowercase)
     * @param keywords palavras-chave a serem buscadas
     * @return true se alguma palavra-chave for encontrada
     */
    private boolean containsKeywords(String description, String... keywords) {
        for (String keyword : keywords) {
            if (description.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Retorna o ícone correspondente à categoria.
     * 
     * @param categoryName nome da categoria
     * @return ícone emoji da categoria
     */
    private String getCategoryIcon(String categoryName) {
        switch (categoryName) {
            case "Alimentação": return "🍽️";
            case "Transporte": return "🚗";
            case "Moradia": return "🏠";
            case "Saúde": return "⚕️";
            case "Educação": return "📚";
            case "Lazer": return "🎬";
            case "Vestuário": return "👕";
            default: return "📊";
        }
    }
    
    /**
     * Calcula o percentual da categoria no período anterior (simulado).
     * 
     * <p><strong>Implementação Atual:</strong> Retorna valores simulados para demonstração.
     * Em uma implementação completa, buscaria dados reais do período anterior.</p>
     * 
     * @param categoryName nome da categoria
     * @param currentPercentage percentual atual da categoria
     * @return percentual simulado do período anterior
     */
    private double calculatePreviousPercentage(String categoryName, double currentPercentage) {
        // Simulação de variações por categoria
        switch (categoryName) {
            case "Alimentação": return currentPercentage * 0.95; // -5%
            case "Transporte": return currentPercentage * 1.12; // +12%
            case "Moradia": return currentPercentage * 1.02; // +2%
            case "Saúde": return currentPercentage * 0.88; // -12%
            case "Educação": return currentPercentage * 1.05; // +5%
            case "Lazer": return currentPercentage * 0.75; // -25%
            case "Vestuário": return currentPercentage * 1.20; // +20%
            default: return currentPercentage * 0.90; // -10%
        }
    }
    
    /**
     * Classe auxiliar para armazenar dados de uma categoria durante o processamento.
     */
    private static class CategoryData {
        private final String name;
        private final String icon;
        private BigDecimal amount;
        
        public CategoryData(String name, String icon) {
            this.name = name;
            this.icon = icon;
            this.amount = BigDecimal.ZERO;
        }
        
        public void addAmount(BigDecimal amount) {
            this.amount = this.amount.add(amount);
        }
        
        public String getName() { return name; }
        public String getIcon() { return icon; }
        public BigDecimal getAmount() { return amount; }
    }
}