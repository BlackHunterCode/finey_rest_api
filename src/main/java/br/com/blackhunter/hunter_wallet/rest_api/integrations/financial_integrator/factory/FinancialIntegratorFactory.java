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

/**
 * <p>Classe <code>FinancialIntegratorFactory</code>.</p>
 * */
public class FinancialIntegratorFactory {
    private FinancialIntegratorFactory() { }

    public static FinancialIntegrator getFinancialIntegrator(FinancialIntegrationPlatform platform) {
        if (platform == null) {
            throw new IllegalArgumentException("Financial integration platform cannot be null.");
        }

        switch (platform) {
            case PLUGGY:
                return new PluggyFinancialIntegrator();
            default:
                throw new UnsupportedOperationException("Financial integration platform not supported: " + platform);
        }
    }
}
