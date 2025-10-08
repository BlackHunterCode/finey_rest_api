package br.com.blackhunter.finey.rest.finance.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Classe de DTO para a entidade Goal.
 * Essa classe carrega dados criptografados.
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalData {
    private UUID goalId;
    private String goalNameEncrypted;
    private String goalDescriptionEncrypted;
    private String goalIconEncrypted;
    private String goalColorEncrypted;
    private String dGoalTargetAmountEncrypted;
    private LocalDate goalDate;
}
