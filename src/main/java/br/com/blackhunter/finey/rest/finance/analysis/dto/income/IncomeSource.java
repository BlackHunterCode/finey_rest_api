package br.com.blackhunter.finey.rest.finance.analysis.dto.income;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSource {
    private String name;
    private String amount;
    private String percentage;
    private boolean isRecurring;
    private String icon;
}
