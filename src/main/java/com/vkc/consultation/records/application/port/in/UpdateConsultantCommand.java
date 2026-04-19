package com.vkc.consultation.records.application.port.in;

public record UpdateConsultantCommand(
        String code,
        String name,
        String speciality,
        String qualification,
        int experienceYears,
        double fee) {}
