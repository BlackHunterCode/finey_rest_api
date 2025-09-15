package br.com.blackhunter.finey.rest.finance.analysis.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetReality {
    private List<BudgetCategory> budgetCategories;
}
