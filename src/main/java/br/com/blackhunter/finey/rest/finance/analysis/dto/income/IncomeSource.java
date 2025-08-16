package br.com.blackhunter.finey.rest.finance.analysis.dto.income;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSource {
    private String name;
    private double amount;
    private double percentage;
    private boolean isRecurring;
    private String icon;
}
