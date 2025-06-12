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

public class PluggyFinancialIntegrator implements FinancialIntegrator {

    @Override
    public boolean connect() {

        // pede credenciais para o usuário.
        // verificar se precisa revogar o token
        // se sim, revoga.
        // se nao prossegue com o
        return false;
    }
}
