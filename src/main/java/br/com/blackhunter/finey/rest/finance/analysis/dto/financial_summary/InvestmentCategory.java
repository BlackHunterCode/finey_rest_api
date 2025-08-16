package br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentCategory {
    private String name;
    private boolean active;
}
