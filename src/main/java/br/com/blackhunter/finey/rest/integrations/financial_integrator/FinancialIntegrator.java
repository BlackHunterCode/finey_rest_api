/*
 * @(#)FinancialIntegrator.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.integrations.financial_integrator;

import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.dto.FinancialInstitutionData;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.dto.IntegrationConnectStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialIntegrator {
    IntegrationConnectStatus connect();
    List<FinancialInstitutionData> getAllConnectedBanks(UUID userId);
    List<TransactionEntity> getAllTransactionsPeriodByTargetId(final String accountId, final LocalDate startDate, final LocalDate endDate);
    String getOriginalFinancialAccountIdByTargetId(final UUID targetId);
}
