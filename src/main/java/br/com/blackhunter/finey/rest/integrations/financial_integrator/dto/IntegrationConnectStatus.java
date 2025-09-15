/*
 * @(#)IntegrationConnectStatus.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.integrations.financial_integrator.dto;

import br.com.blackhunter.finey.rest.integrations.financial_integrator.enums.FinancialIntegrationPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationConnectStatus {
    private String message;
    private String dataAccess;
    private FinancialIntegrationPlatform platform;
    private LocalDateTime expiredAt;
}
