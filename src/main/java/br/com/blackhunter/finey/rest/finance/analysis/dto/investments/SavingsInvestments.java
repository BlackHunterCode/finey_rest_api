package br.com.blackhunter.finey.rest.finance.analysis.dto.investments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsInvestments {
    String totalInvested;
    String totalReturn;
    String totalReturnPercentage;
    List<Investment> investments;
}
