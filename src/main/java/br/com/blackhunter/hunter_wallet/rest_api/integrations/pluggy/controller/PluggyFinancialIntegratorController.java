package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import br.com.blackhunter.hunter_wallet.rest_api.core.dto.ApiResponse;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto.PluggyItemIdPayload;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto.PluggyItemIdResponse;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;

@RestController
@RequestMapping("/v1/integrations/financial-integrator/pluggy")
public class PluggyFinancialIntegratorController {
    private final PluggyAccessService pluggyAccessService;

    public PluggyFinancialIntegratorController(PluggyAccessService pluggyAccessService) {
        this.pluggyAccessService = pluggyAccessService;
    }

    @PostMapping("/save-item-id")
    public ResponseEntity<ApiResponse<PluggyItemIdResponse>> savePluggyItemId(@Valid @RequestBody PluggyItemIdPayload payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<PluggyItemIdResponse>(
            "success", 
            HttpStatus.CREATED.value(), 
            pluggyAccessService.savePluggyItemId(payload)
        ));
    }
}