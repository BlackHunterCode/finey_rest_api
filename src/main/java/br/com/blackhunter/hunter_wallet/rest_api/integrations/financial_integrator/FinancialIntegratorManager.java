/*
 * @(#)FinancialIntegratorManager.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.enums.FinancialIntegrationPlatform;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.factory.FinancialIntegratorFactory;
import org.springframework.stereotype.Component;

@Component
public class FinancialIntegratorManager {
    /* A plataforma de integração financeira que será usada */
    private final FinancialIntegrationPlatform financialIntegrationPlatform = FinancialIntegrationPlatform.PLUGGY;

    private final FinancialIntegratorFactory financialIntegratorFactory;

    public FinancialIntegratorManager(FinancialIntegratorFactory financialIntegratorFactory) {
        this.financialIntegratorFactory = financialIntegratorFactory;
    }

    /**
     * Obtém uma instância de <code>FinancialIntegrator</code> com base na plataforma de integração financeira configurada.
     *
     * @return Uma instância de <code>FinancialIntegrator</code>.
     */
    public FinancialIntegrator getFinancialIntegrator() {
        return financialIntegratorFactory.getFinancialIntegrator(financialIntegrationPlatform);
    }
}
