/*
 * @(#)FinancialIntegratorFactory.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.factory;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.enums.FinancialIntegrationPlatform;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.PluggyFinancialIntegrator;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;
import org.springframework.stereotype.Component;

/**
 * <p>Classe <code>FinancialIntegratorFactory</code>.</p>
 * */
@Component
public class FinancialIntegratorFactory {
    private final PluggyAccessService pluggyAccessService;

    public FinancialIntegratorFactory(PluggyAccessService pluggyAccessService) {
        this.pluggyAccessService = pluggyAccessService;
    }

    /**
     * <p>Cria uma instância de <code>FinancialIntegrator</code> com base na plataforma de integração financeira fornecida.</p>
     *
     * @param platform A plataforma de integração financeira.
     * @return Uma instância de <code>FinancialIntegrator</code>.
     * @throws IllegalArgumentException Se a plataforma for nula.
     * @throws UnsupportedOperationException Se a plataforma não for suportada.
     */
    public FinancialIntegrator getFinancialIntegrator(FinancialIntegrationPlatform platform) {
        if (platform == null) {
            throw new IllegalArgumentException("Financial integration platform cannot be null.");
        }

        switch (platform) {
            case PLUGGY:
                return new PluggyFinancialIntegrator(pluggyAccessService);
            default:
                throw new UnsupportedOperationException("Financial integration platform not supported: " + platform);
        }
    }
}
