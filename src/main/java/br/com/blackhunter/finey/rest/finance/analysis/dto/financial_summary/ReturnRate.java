package br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRate {
    private String percentage;
    private String status;
}
