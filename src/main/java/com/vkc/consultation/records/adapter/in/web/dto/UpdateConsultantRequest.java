package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.domain.model.SpecialityType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for updating an existing consultant")
public record UpdateConsultantRequest(
        @Schema(description = "Full name of the consultant", example = "Dr. Jane Smith") String name,
        @Schema(description = "Email address used for consultant login", example = "jane.smith@clinic.com") String email,
        @Schema(description = "Mobile number", example = "+1-555-123-4567") String mobile,
        @Schema(description = "Medical speciality", example = "PEDIATRICIAN") SpecialityType speciality,
        @Schema(description = "Highest qualification", example = "MBBS, MD") String qualification,
        @Schema(description = "Years of professional experience", example = "10") int experienceYears,
        @Schema(description = "Consultation fee", example = "150.00") double fee) {}
