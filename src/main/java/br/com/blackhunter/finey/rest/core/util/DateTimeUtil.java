/*
 * @(#)DateTimeUtil.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.core.util;

import java.time.Duration;
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
}
