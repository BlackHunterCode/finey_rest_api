/*
 * @(#)TransactionServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.finance.transaction.service.impl;

import br.com.blackhunter.finey.rest.auth.util.CryptUtil;
import br.com.blackhunter.finey.rest.auth.util.JwtUtil;
import br.com.blackhunter.finey.rest.core.dto.TransactionPeriodDate;
import br.com.blackhunter.finey.rest.core.util.DateTimeUtil;
import br.com.blackhunter.finey.rest.finance.transaction.dto.TotalTransactionsPeriod;
import br.com.blackhunter.finey.rest.finance.transaction.dto.TransactionData;
import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionType;
import br.com.blackhunter.finey.rest.finance.transaction.mapper.TransactionMapper;
import br.com.blackhunter.finey.rest.finance.transaction.payload.TransactionPayload;
import br.com.blackhunter.finey.rest.finance.transaction.repository.TransactionRepository;
import br.com.blackhunter.finey.rest.finance.transaction.service.TransactionService;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.FinancialIntegratorManager;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>Classe <code>TransactionServiceImpl</code>.</p>
 * <p>Essa classe implementa o processamento e as regras de negócio dos serviços de transações</p>
 * */
@Service
public class TransactionServiceImpl implements TransactionService {
    @Value("${hunter.secrets.pluggy.crypt-secret}")
    private String PLUGGY_CRYPT_SECRET;

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final JwtUtil jwtUtil;
    private final FinancialIntegratorManager financialIntegratorManager;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            JwtUtil jwtUtil,
            FinancialIntegratorManager financialIntegratorManager
    ) {
        this.transactionRepository      = transactionRepository;
        this.transactionMapper          = transactionMapper;
        this.jwtUtil                    = jwtUtil;
        this.financialIntegratorManager = financialIntegratorManager;
    }

    /**
     * @param transactionPayload payload contendo os dados da transação a ser registrada.
     * <p>Esse método registra uma transação no banco de dados.</p>
     * <p>Uma transação pode ser um gasto ou um ganho.</p>
     * <p>
     *   Obs: Esse método vai ser mais usado em um evento de <code>Webhook</code>
     *   ou manualmente pelo usuário através da <code>API REST</code>.
     * </p>
     *
     * @author Victor Barberino
     * @return Retorna os dados da transação registrada.
     * */
    @Override
    public TransactionData registerTransaction(@Validated TransactionPayload transactionPayload) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionPayload);
        UserAccountEntity userAccountEntity =  jwtUtil.getUserAccountFromToken();
        transactionEntity.setUserAccount(userAccountEntity);
        return transactionMapper.toData(transactionRepository.save(transactionEntity));
    }

    /**
     * Calcula o total de ganhos e gastos para um período específico
     *
     * @param bankAccountIds ID da conta bancária
     * @param referenceDateMonthYear Data de referência (opcional, pode ser usado para filtros adicionais)
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return Objeto com os totais calculados
     */
    @Override
    public TotalTransactionsPeriod getTotalTransactionsPeriod(List<String> bankAccountIds, LocalDate referenceDateMonthYear, LocalDate startDate, LocalDate endDate) {
        List<TransactionData> allTransactions = new ArrayList<>();
        for(String accountId: bankAccountIds) {
            allTransactions.addAll(getAllTransactionsPeriodByAccountId(accountId, referenceDateMonthYear, startDate, endDate));
        }
        return calculateTotals(allTransactions);
    }

    /**
     * Método auxiliar para calcular os totais de ganhos e gastos
     */
    private TotalTransactionsPeriod calculateTotals(List<TransactionData> transactions) {
        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (TransactionData transaction : transactions) {
            if (transaction.getType() == TransactionType.CREDIT) {
                // entrada
                totalEarnings = totalEarnings.add(transaction.getAmount());
            } else if (transaction.getType() == TransactionType.DEBIT) {
                // saida
                // Garantir que valores de débito sejam positivos no total
                totalExpenses = totalExpenses.add(transaction.getAmount().abs());
            }
            // Ignora outros tipos se houver
        }

        return new TotalTransactionsPeriod(totalEarnings, totalExpenses);
    }

    public List<TransactionData> getAllTransactionsPeriodByAccountId(String accountId, LocalDate referenceDateMonthYear, LocalDate startDate, LocalDate endDate) {
        try {
            String accountEntityId = CryptUtil.decrypt(accountId, PLUGGY_CRYPT_SECRET);
            TransactionPeriodDate transactionPeriodDate = DateTimeUtil.getTransactionPeriodDate(referenceDateMonthYear, startDate, endDate);

            // buscar transações no período
            List<TransactionEntity> transactions = transactionRepository.findAllByFinancialAccountIdAndDateBetween(UUID.fromString(accountEntityId), transactionPeriodDate.getStartDate(), transactionPeriodDate.getEndDate());

            // buscar na API d pluggy
            if(transactions.isEmpty()) {
                FinancialIntegrator financialIntegrator = financialIntegratorManager.getFinancialIntegrator();
                String originalPluggyAccountId = financialIntegrator.getOriginalFinancialAccountIdByTargetId(UUID.fromString(accountEntityId));
                transactions = getAllTransactionsPeriodByAccountIdFromFinancialIntegrator(
                        originalPluggyAccountId,
                        transactionPeriodDate.getStartDate(),
                        transactionPeriodDate.getEndDate()
                );
            }

            return transactions.stream().map(transactionMapper::toData).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /* Métodos privados */

    private List<TransactionEntity> getAllTransactionsPeriodByAccountIdFromFinancialIntegrator(String accountId, LocalDate startDate, LocalDate endDate) {
        FinancialIntegrator financialIntegrator = financialIntegratorManager.getFinancialIntegrator();
        return financialIntegrator.getAllTransactionsPeriodByTargetId(
                accountId,
                startDate,
                endDate
        );
    }
}
