package br.com.blackhunter.finey.rest.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPeriodDate {
    private LocalDate startDate;
    private LocalDate endDate;
}