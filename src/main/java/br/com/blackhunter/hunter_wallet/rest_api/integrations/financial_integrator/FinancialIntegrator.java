/*
 * @(#)FinancialIntegrator.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.dto.IntegrationConnectStatus;

import java.util.List;
import java.util.UUID;

public interface FinancialIntegrator {
    IntegrationConnectStatus connect();
    List<String> getAllConnectedBanks(UUID userId);
}
