package com.vkc.consultation.records.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.vkc.consultation.records.application.domain.model.Consultation;

public record ConsultationResponse(
        String id,
        String code,
        String type,
        String consultantCode,
        String patientCode,
        String diagnosis,
        String prescription,
        String comments,
        Date consultationDate,
        Date followUpDate,
        Date updatedDate,
        String createdBy,
        BigDecimal fee) {

    public static ConsultationResponse from(Consultation c) {
        return new ConsultationResponse(
                c.getId(),
                c.getCode(),
                c.getType(),
                c.getConsultantCode(),
                c.getPatientCode(),
                c.getDiagnosis(),
                c.getPrescription(),
                c.getComments(),
                c.getConsultationDate(),
                c.getFollowUpDate(),
                c.getUpdatedDate(),
                c.getCreatedBy(),
                c.getFee());
    }
}
