package br.com.blackhunter.finey.rest.finance.analysis.dto.financial_summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeExpenseData {
    private String value;
    private String updatedAt;
    private String status;
    private String percentage;
}
