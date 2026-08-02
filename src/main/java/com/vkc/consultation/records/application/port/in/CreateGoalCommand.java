package com.vkc.consultation.records.application.port.in;

import java.util.Date;

public record CreateGoalCommand(
        String code,
        String name,
        String description,
        String importance,
        String difficulty,
        int achievingAgeYears,
        int achievingAgeMonths,
        String remarks,
        int periodInMonths,
        Date createdDate,
        String status) {}
