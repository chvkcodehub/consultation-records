package com.vkc.consultation.records.adapter.in.web.dto;

public record UpdateConsultantRequest(
        String code,
        String name,
        String speciality,
        String qualification,
        int experienceYears,
        double fee) {}
