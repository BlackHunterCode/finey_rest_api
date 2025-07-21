/*
 * @(#)PluggyFinancialIntegrator.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.dto.IntegrationConnectStatus;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.enums.FinancialIntegrationPlatform;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;

public class PluggyFinancialIntegrator implements FinancialIntegrator {
    private final PluggyAccessService pluggyAccessService;

    public PluggyFinancialIntegrator(PluggyAccessService pluggyAccessService) {
        this.pluggyAccessService = pluggyAccessService;
    }
    @Override
    public IntegrationConnectStatus connect() {
        System.out.println("Connect method called");
        String tokenEncrypted = pluggyAccessService.getAndSaveAccessTokenEncryptedIfNecessary();
        System.out.println("Token Encrypted: " + tokenEncrypted);
        String connectTokenEncrypted = pluggyAccessService.getConnectTokenEncrypted(tokenEncrypted);

        return new IntegrationConnectStatus(
                "bank connection token created successfully.",
                connectTokenEncrypted,
                FinancialIntegrationPlatform.PLUGGY,
                LocalDateTime.now().plusMinutes(25)
        );
    }

    @Override
    public List<String> getAllConnectedBanks(UUID userId) {
        return pluggyAccessService.getAllItemsByUserId(userId);
    }
}
