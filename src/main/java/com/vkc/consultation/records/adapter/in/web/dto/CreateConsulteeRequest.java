package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for creating a new consultee (patient)")
public record CreateConsulteeRequest(
        @Schema(description = "Unique business code", example = "PT001") String code,
        @Schema(description = "Full name of the consultee", example = "John Doe") String name,
        @Schema(description = "Gender", example = "Male") String gender,
        @Schema(description = "Date of birth", example = "1990-05-15") Date dob,
        @Schema(description = "Medical condition", example = "Hypertension") String condition,
        @Schema(description = "Residential address", example = "123 Main St, Springfield") String address,
        @Schema(description = "Contact phone number", example = "+1-555-0100") String phone,
        @Schema(description = "Email address", example = "john.doe@example.com") String email,
        @Schema(description = "Date care/treatment started", example = "2024-01-10") Date startDate) {}
