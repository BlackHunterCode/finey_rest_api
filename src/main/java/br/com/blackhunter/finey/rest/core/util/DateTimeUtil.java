/*
 * @(#)DateTimeUtil.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.core.util;

import br.com.blackhunter.finey.rest.core.dto.TransactionPeriodDate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeUtil {
    public static String calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return "Duração não disponível";
        }

        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Retorna um objeto TransactionPeriodDate com as datas de início e fim do período de transações.
     * Esse método segue regras de negócio específicas para determinar o período com base nas datas fornecidas.
     *
     * @param referenceDateMonthYear Data de referência no formato LocalDate (apenas mês e ano são considerados).
     * @param startDate Data de início para um período personalizado.
     * @param endDate Data de fim para um período personalizado.
     *
     * @return um objeto TransactionPeriodDate com as datas de inicio e fim formatadas.
     * */
    public static TransactionPeriodDate getTransactionPeriodDate(LocalDate referenceDateMonthYear, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && (startDate.isBefore(endDate) || startDate.isEqual(endDate))) {
            return new TransactionPeriodDate(startDate, endDate);
        } else if (referenceDateMonthYear != null) {
            int referenceYear = referenceDateMonthYear.getYear();
            int referenceMonth = referenceDateMonthYear.getMonthValue();
            LocalDate today = LocalDate.now();

            if (referenceYear == today.getYear() && referenceMonth > today.getMonthValue()) {
                throw new IllegalArgumentException("Reference date cannot be in the future.");
            }

            LocalDate start = referenceDateMonthYear.withDayOfMonth(1);
            LocalDate end = (referenceYear == today.getYear() && referenceMonth == today.getMonthValue())
                    ? today
                    : referenceDateMonthYear.withDayOfMonth(referenceDateMonthYear.lengthOfMonth());

            return new TransactionPeriodDate(start, end);
        } else {
            throw new IllegalArgumentException("Invalid date parameters provided.");
        }
    }
}
