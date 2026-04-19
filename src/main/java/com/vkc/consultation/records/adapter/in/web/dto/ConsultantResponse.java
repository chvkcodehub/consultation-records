package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.domain.model.Consultant;

public record ConsultantResponse(
        String id,
        String code,
        String name,
        String speciality,
        String qualification,
        int experienceYears,
        double fee) {

    public static ConsultantResponse from(Consultant c) {
        return new ConsultantResponse(
                c.getId(),
                c.getCode(),
                c.getName(),
                c.getSpeciality(),
                c.getQualification(),
                c.getExperienceYears(),
                c.getFee());
    }
}
