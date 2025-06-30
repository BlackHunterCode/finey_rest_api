package br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.controller;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.ApiResponse;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.dto.IntegrationConnectStatus;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.financial_integrator.service.FinancialIntegratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/integrations/financial-integrator")
public class FinancialIntegratorController {
    private final FinancialIntegratorService financialIntegratorService;

    public FinancialIntegratorController(FinancialIntegratorService financialIntegratorService) {
        this.financialIntegratorService = financialIntegratorService;
    }

    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<IntegrationConnectStatus>> connect() {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        "success",
                        HttpStatus.CREATED.value(),
                        financialIntegratorService.connect()
                ));
    }
}
