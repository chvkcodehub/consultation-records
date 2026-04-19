package com.vkc.consultation.records.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for updating an existing consultant")
public record UpdateConsultantRequest(
        @Schema(description = "Unique business code", example = "CON001") String code,
        @Schema(description = "Full name of the consultant", example = "Dr. Jane Smith") String name,
        @Schema(description = "Medical speciality", example = "Cardiology") String speciality,
        @Schema(description = "Highest qualification", example = "MBBS, MD") String qualification,
        @Schema(description = "Years of professional experience", example = "10") int experienceYears,
        @Schema(description = "Consultation fee", example = "150.00") double fee) {}
