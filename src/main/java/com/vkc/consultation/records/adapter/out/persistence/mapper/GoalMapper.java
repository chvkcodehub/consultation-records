package com.vkc.consultation.records.adapter.out.persistence.mapper;

import com.vkc.consultation.records.adapter.out.persistence.entity.GoalDocument;
import com.vkc.consultation.records.application.domain.model.Goal;

public class GoalMapper {

    private GoalMapper() {}

    public static Goal toDomain(GoalDocument doc) {
        Goal goal = new Goal();
        goal.setId(doc.getId());
        goal.setCode(doc.getCode());
        goal.setName(doc.getName());
        goal.setDescription(doc.getDescription());
        goal.setImportance(doc.getImportance());
        goal.setDifficulty(doc.getDifficulty());
        goal.setAchievingAgeYears(doc.getAchievingAgeYears());
        goal.setAchievingAgeMonths(doc.getAchievingAgeMonths());
        goal.setRemarks(doc.getRemarks());
        goal.setPeriodInMonths(doc.getPeriodInMonths());
        goal.setCreatedDate(doc.getCreatedDate());
        goal.setUpdatedDate(doc.getUpdatedDate());
        goal.setStatus(doc.getStatus());
        return goal;
    }

    public static GoalDocument toDocument(Goal goal) {
        GoalDocument doc = new GoalDocument();
        doc.setId(goal.getId());
        doc.setCode(goal.getCode());
        doc.setName(goal.getName());
        doc.setDescription(goal.getDescription());
        doc.setImportance(goal.getImportance());
        doc.setDifficulty(goal.getDifficulty());
        doc.setAchievingAgeYears(goal.getAchievingAgeYears());
        doc.setAchievingAgeMonths(goal.getAchievingAgeMonths());
        doc.setRemarks(goal.getRemarks());
        doc.setPeriodInMonths(goal.getPeriodInMonths());
        doc.setCreatedDate(goal.getCreatedDate());
        doc.setUpdatedDate(goal.getUpdatedDate());
        doc.setStatus(goal.getStatus());
        return doc;
    }
}
