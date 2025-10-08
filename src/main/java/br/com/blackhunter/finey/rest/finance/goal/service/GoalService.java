package br.com.blackhunter.finey.rest.finance.goal.service;

import br.com.blackhunter.finey.rest.finance.goal.dto.GoalData;
import br.com.blackhunter.finey.rest.finance.goal.dto.GoalPayload;

import java.util.List;
import java.util.UUID;

public interface GoalService {
    GoalData persistGoal(GoalPayload payload, UUID goalId);
    List<GoalData> listAllUserGoals();
}
