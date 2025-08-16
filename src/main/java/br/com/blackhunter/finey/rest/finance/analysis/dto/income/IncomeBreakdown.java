package br.com.blackhunter.finey.rest.finance.analysis.dto.income;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeBreakdown {
    private List<IncomeSource> incomeSources;
    private double totalIncome;
    private double recurringIncome;
    private double variableIncom;
}
