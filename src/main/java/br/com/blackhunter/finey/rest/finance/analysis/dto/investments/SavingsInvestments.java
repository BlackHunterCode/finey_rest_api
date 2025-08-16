package br.com.blackhunter.finey.rest.finance.analysis.dto.investments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsInvestments {
    double totalInvested;
    double totalReturn;
    double totalReturnPercentage;
    List<Investment> investments;
}
