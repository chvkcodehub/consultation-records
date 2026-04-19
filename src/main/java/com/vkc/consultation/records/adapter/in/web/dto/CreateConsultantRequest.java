package com.vkc.consultation.records.adapter.in.web.dto;

public record CreateConsultantRequest(
        String code,
        String name,
        String speciality,
        String qualification,
        int experienceYears,
        double fee) {}
