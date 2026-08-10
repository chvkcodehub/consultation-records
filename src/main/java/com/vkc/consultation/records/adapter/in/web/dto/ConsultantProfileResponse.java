package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.domain.model.Consultant;

public record ConsultantProfileResponse(
        String id,
        String name,
        String email,
        String mobile,
        String speciality,
        String qualification,
        int experienceYears,
        double fee) {

    public static ConsultantProfileResponse from(Consultant consultant) {
        return new ConsultantProfileResponse(
                consultant.getId(),
                consultant.getName(),
                consultant.getEmail(),
                consultant.getMobile(),
                consultant.getSpeciality() != null ? consultant.getSpeciality().name() : null,
                consultant.getQualification(),
                consultant.getExperienceYears(),
                consultant.getFee());
    }
}