package br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentData {
    private String value;
    private String updatedAt;
    private List<InvestmentCategory> categories;
    private String returnRate;
    private String status;
}
