package br.com.blackhunter.finey.rest.finance.analysis.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCategory {
    private String name;
    private String icon;
    private double budgetAmount;
    private double spentAmount;
    private double percentage;
}
