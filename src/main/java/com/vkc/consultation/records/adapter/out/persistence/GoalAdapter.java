package com.vkc.consultation.records.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.vkc.consultation.records.adapter.out.persistence.mapper.GoalMapper;
import com.vkc.consultation.records.application.domain.model.Goal;
import com.vkc.consultation.records.application.port.out.GoalPort;

@Component
public class GoalAdapter implements GoalPort {

    private final GoalRepository goalRepository;

    public GoalAdapter(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public List<Goal> findAll() {
        return goalRepository.findAll().stream()
                .map(GoalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Goal> findById(@NonNull String id) {
        return goalRepository.findById(id).map(GoalMapper::toDomain);
    }

    @Override
    public Goal save(@NonNull Goal goal) {
        return GoalMapper.toDomain(goalRepository.save(GoalMapper.toDocument(goal)));
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return goalRepository.existsById(id);
    }

    @Override
    public void deleteById(@NonNull String id) {
        goalRepository.deleteById(id);
    }
}
