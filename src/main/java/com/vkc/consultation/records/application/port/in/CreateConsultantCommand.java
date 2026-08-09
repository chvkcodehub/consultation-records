package com.vkc.consultation.records.application.port.in;

import com.vkc.consultation.records.application.domain.model.SpecialityType;

public record CreateConsultantCommand(
        String name,
        SpecialityType speciality,
        String qualification,
        int experienceYears,
        double fee) {}
