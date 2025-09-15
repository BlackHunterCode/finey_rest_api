/*
 * @(#)PluggyAccessService.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.integrations.pluggy.service;

import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.dto.FinancialInstitutionData;
import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyItemIdPayload;
import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyItemIdResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PluggyAccessService {
    String getAndSaveAccessTokenEncryptedIfNecessary();
    String getConnectTokenEncrypted(String token);
    PluggyItemIdResponse savePluggyItemId(PluggyItemIdPayload payload);
    List<FinancialInstitutionData> getAllItemsByUserId(UUID userId);
    String getOriginalPluggyAccountIdByEntityId(UUID entityId);
    List<TransactionEntity> getAllTransactionsPeriodByOriginalAccountId(final String originalAccountId, final LocalDate startDate, final LocalDate endDate);
}
