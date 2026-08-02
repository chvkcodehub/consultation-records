package com.vkc.consultation.records.application.port.in;

import com.vkc.consultation.records.application.domain.model.SpecialityType;

public record UpdateConsultantCommand(
        String name,
        SpecialityType speciality,
        String qualification,
        int experienceYears,
        double fee) {}
