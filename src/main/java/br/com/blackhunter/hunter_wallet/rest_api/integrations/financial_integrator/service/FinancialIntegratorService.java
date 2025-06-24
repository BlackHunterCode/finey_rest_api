package br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.service;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegrator;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.FinancialIntegratorManager;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.dto.IntegrationConnectStatus;
import org.springframework.stereotype.Service;

@Service
public class FinancialIntegratorService {
    private final FinancialIntegratorManager financialIntegratorManager;

    public FinancialIntegratorService(FinancialIntegratorManager financialIntegratorManager) {
        this.financialIntegratorManager = financialIntegratorManager;
    }

    public IntegrationConnectStatus connect() {
        FinancialIntegrator financialIntegrator = getFinancialIntegrator();
        return financialIntegrator.connect();
    }

    /* Métodos privados. */

    private FinancialIntegrator getFinancialIntegrator() {
        return financialIntegratorManager.getFinancialIntegrator();
    }
}
