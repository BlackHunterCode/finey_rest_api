package br.com.blackhunter.finey.rest.finance.analysis.dto.expanses_categories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpensesCategories {
    private List<ExpenseCategory> categories;
    private double totalExpenses;
}
