package br.com.blackhunter.finey.rest.integrations.financial_integrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTransaction {
    private String id;
    private String description;
    private Double amount;
    private String currencyCode;
    private String category;
}
