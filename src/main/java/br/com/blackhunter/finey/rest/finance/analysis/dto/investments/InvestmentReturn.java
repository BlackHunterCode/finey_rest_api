package br.com.blackhunter.finey.rest.finance.analysis.dto.investments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentReturn {
    private String value;
    private String percentage;
    private boolean isPositive;
}
