package br.com.blackhunter.finey.rest.finance.analysis.dto.investments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Investment {
    private String type;
    private String amount;
    private InvestmentReturn investmentReturn;
    private String icon;
}
