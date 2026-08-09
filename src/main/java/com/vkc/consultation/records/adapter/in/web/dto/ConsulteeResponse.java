package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.Date;

import com.vkc.consultation.records.application.domain.model.Consultee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Consultee details returned by the API")
public record ConsulteeResponse(
        @Schema(description = "MongoDB document ID", example = "6624a1f3e2b4c30012d4f902") String id,
        @Schema(description = "Full name of the consultee", example = "John Doe") String name,
        @Schema(description = "Gender", example = "Male") String gender,
        @Schema(description = "Date of birth") Date dob,
               @Schema(description = "Residential address", example = "123 Main St, Springfield") String address,
        @Schema(description = "Email address", example = "john.doe@example.com") String email,
        @Schema(description = "Contact phone number", example = "+1-555-0100") String phone,
        
        @Schema(description = "Date care/treatment started") Date startDate,
         @Schema(description = "Medical condition", example = "Hypertension") String condition,
        @Schema(description = "Recovery status", example = "Recovered") String recoveryStatus) {

    public static ConsulteeResponse from(Consultee c) {
        return new ConsulteeResponse(
                c.getId(),
                c.getName(),
                c.getGender(),
                c.getDob(),                
                c.getAddress(),
                c.getEmail(),
                c.getPhone(),                
                c.getStartDate(),
                c.getCondition(), 
                c.getRecoveryStatus());
    }
}
