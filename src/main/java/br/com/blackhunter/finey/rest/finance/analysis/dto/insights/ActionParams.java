package br.com.blackhunter.finey.rest.finance.analysis.dto.insights;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionParams {
    private String category;
    private double currentSpending;
    private String period;
}
