package com.vkc.consultation.records.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.domain.model.ConsultationType;

public record ConsultationResponse(
        String id,
        ConsultationType type,
        ConsultationStatus status,
        String consultantId,
        String consultantName,
        String consulteeId,
        String consulteeName,
        String diagnosis,
        String prescription,
        String comments,
        Integer rating,
        String feedback,
        Date consultationDate,
        Date followUpDate,
        Date updatedDate,
        String createdBy,
        BigDecimal fee) {

    public static ConsultationResponse from(Consultation c, Map<String, String> consultantNamesById,
            Map<String, String> consulteeNamesById) {
        return new ConsultationResponse(
                c.getId(),
                c.getType(),
                c.getStatus(),
                c.getConsultantId(),
                consultantNamesById.get(c.getConsultantId()),
                c.getConsulteeId(),
                consulteeNamesById.get(c.getConsulteeId()),
                c.getDiagnosis(),
                c.getPrescription(),
                c.getComments(),
                c.getRating(),
                c.getFeedback(),
                c.getConsultationDate(),
                c.getFollowUpDate(),
                c.getUpdatedDate(),
                c.getCreatedBy(),
                c.getFee());
    }
}
