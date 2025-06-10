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
import org.springframework.beans.factory.annotation.Value;

public class PluggyFinancialIntegrator implements FinancialIntegrator {
    @Value("${hunter.secrets.pluggy.client-id}")
    private String PLUGGY_CLIENT_ID;
    @Value("${hunter.secrets.pluggy.client-secret}")
    private String PLUGGY_CLIENT_SECRET;

    @Override
    public boolean connect() {
        return false;
    }
}
