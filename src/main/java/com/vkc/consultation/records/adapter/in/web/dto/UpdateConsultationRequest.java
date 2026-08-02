package com.vkc.consultation.records.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.domain.model.ConsultationType;

public record UpdateConsultationRequest(
        String code,
        ConsultationType type,
        ConsultationStatus status,
        String consultantCode,
        String patientCode,
        String diagnosis,
        String prescription,
        String comments,
        Date consultationDate,
        Date followUpDate,
        Date updatedDate,
        String createdBy,
        BigDecimal fee) {}
