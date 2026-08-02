package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.Date;

import com.vkc.consultation.records.application.domain.model.Goal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Goal details returned by the API")
public record GoalResponse(
        @Schema(description = "MongoDB document ID", example = "6624a1f3e2b4c30012d4f903") String id,
        @Schema(description = "Unique business code", example = "GL001") String code,
        @Schema(description = "Name of the goal", example = "Reduce blood pressure") String name,
        @Schema(description = "Detailed description of the goal") String description,
        @Schema(description = "Importance level", example = "High") String importance,
        @Schema(description = "Difficulty level", example = "Medium") String difficulty,
        @Schema(description = "Target achieving age in years", example = "45") int achievingAgeYears,
        @Schema(description = "Target achieving age in months", example = "6") int achievingAgeMonths,
        @Schema(description = "Additional remarks") String remarks,
        @Schema(description = "Duration of the goal in months", example = "12") int periodInMonths,
        @Schema(description = "Date the goal was created") Date createdDate,
        @Schema(description = "Date the goal was last updated") Date updatedDate,
        @Schema(description = "Current status of the goal", example = "Active") String status) {

    public static GoalResponse from(Goal g) {
        return new GoalResponse(
                g.getId(),
                g.getCode(),
                g.getName(),
                g.getDescription(),
                g.getImportance(),
                g.getDifficulty(),
                g.getAchievingAgeYears(),
                g.getAchievingAgeMonths(),
                g.getRemarks(),
                g.getPeriodInMonths(),
                g.getCreatedDate(),
                g.getUpdatedDate(),
                g.getStatus());
    }
}
