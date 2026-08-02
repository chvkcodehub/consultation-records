package com.vkc.consultation.records.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Goal;

public interface GoalPort {
    List<Goal> findAll();
    Optional<Goal> findById(@NonNull String id);
    Optional<Goal> findByCode(String code);
    Goal save(@NonNull Goal goal);
    boolean existsById(@NonNull String id);
    void deleteById(@NonNull String id);
}
