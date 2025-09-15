package br.com.blackhunter.finey.rest.finance.analysis.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisPayload {
    private List<String> bankAccountIds;
    private LocalDate referenceDateMonthYear;
    private LocalDate startDate;
    private LocalDate endDate;
}
