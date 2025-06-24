/*
 * @(#)PluggyFinancialIntegrator.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.dto.IntegrationConnectStatus;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;

import java.time.LocalDateTime;

public class PluggyFinancialIntegrator implements FinancialIntegrator {
    private final PluggyAccessService pluggyAccessService;

    public PluggyFinancialIntegrator(PluggyAccessService pluggyAccessService) {
        this.pluggyAccessService = pluggyAccessService;
    }
    @Override
    public IntegrationConnectStatus connect() {
        String tokenEncrypted = pluggyAccessService.getAndSaveAccessTokenEncryptedIfNecessary();
        String connectTokenEncrypted = pluggyAccessService.getConnectTokenEncrypted(tokenEncrypted);

        return new IntegrationConnectStatus(
                "bank connection token created successfully.",
                connectTokenEncrypted,
                LocalDateTime.now().plusMinutes(25)
        );
    }
}
