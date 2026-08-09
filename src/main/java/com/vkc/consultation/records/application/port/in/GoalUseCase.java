package com.vkc.consultation.records.application.port.in;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Goal;

public interface GoalUseCase {
    List<Goal> findGoals();
    Goal findGoalById(@NonNull String id);
    Goal createGoal(@NonNull CreateGoalCommand command);
    Goal updateGoal(@NonNull String id, @NonNull UpdateGoalCommand command);
    void deleteGoal(@NonNull String id);
}
