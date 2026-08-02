package com.vkc.consultation.records.application.domain.service;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Goal;
import com.vkc.consultation.records.application.port.in.CreateGoalCommand;
import com.vkc.consultation.records.application.port.in.GoalUseCase;
import com.vkc.consultation.records.application.port.in.UpdateGoalCommand;
import com.vkc.consultation.records.application.port.out.GoalPort;

@Service
public class GoalService implements GoalUseCase {

    private final GoalPort goalPort;

    public GoalService(GoalPort goalPort) {
        this.goalPort = goalPort;
    }

    @Override
    public List<Goal> findGoals() {
        return goalPort.findAll();
    }

    @Override
    public Goal findGoalById(@NonNull String id) {
        return goalPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with id: " + id));
    }

    @Override
    public Goal findGoalByCode(String code) {
        return goalPort.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with code: " + code));
    }

    @Override
    public Goal createGoal(@NonNull CreateGoalCommand command) {
        Goal goal = new Goal();
        goal.setCode(command.code());
        goal.setName(command.name());
        goal.setDescription(command.description());
        goal.setImportance(command.importance());
        goal.setDifficulty(command.difficulty());
        goal.setAchievingAgeYears(command.achievingAgeYears());
        goal.setAchievingAgeMonths(command.achievingAgeMonths());
        goal.setRemarks(command.remarks());
        goal.setPeriodInMonths(command.periodInMonths());
        goal.setCreatedDate(command.createdDate() != null ? command.createdDate() : new Date());
        goal.setUpdatedDate(new Date());
        goal.setStatus(command.status());
        return goalPort.save(goal);
    }

    @Override
    public Goal updateGoal(@NonNull String id, @NonNull UpdateGoalCommand command) {
        if (!goalPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with id: " + id);
        }
        Goal goal = new Goal();
        goal.setId(id);
        goal.setCode(command.code());
        goal.setName(command.name());
        goal.setDescription(command.description());
        goal.setImportance(command.importance());
        goal.setDifficulty(command.difficulty());
        goal.setAchievingAgeYears(command.achievingAgeYears());
        goal.setAchievingAgeMonths(command.achievingAgeMonths());
        goal.setRemarks(command.remarks());
        goal.setPeriodInMonths(command.periodInMonths());
        goal.setUpdatedDate(command.updatedDate() != null ? command.updatedDate() : new Date());
        goal.setStatus(command.status());
        return goalPort.save(goal);
    }

    @Override
    public void deleteGoal(@NonNull String id) {
        if (!goalPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found with id: " + id);
        }
        goalPort.deleteById(id);
    }
}
