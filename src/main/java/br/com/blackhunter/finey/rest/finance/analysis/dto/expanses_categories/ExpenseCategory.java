package br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategory {
    private String name;
    private String icon;
    private double amount;
    private double percentage;
    private double previousPercentage;
}
