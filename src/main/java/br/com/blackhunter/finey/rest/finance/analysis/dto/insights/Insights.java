package br.com.blackhunter.finey.rest.finance.analysis.dto.insights;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Insights {
    private List<Insight> insights;
}
