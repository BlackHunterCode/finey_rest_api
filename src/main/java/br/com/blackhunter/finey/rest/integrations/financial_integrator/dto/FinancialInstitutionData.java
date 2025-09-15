package br.com.blackhunter.finey.rest.integrations.financial_integrator.dto;

import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyAccountIds;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialInstitutionData {
    private String institutionId;
    private String institutionName;
    private String institutionImageUrl;
    private List<PluggyAccountIds> accounts;
}
