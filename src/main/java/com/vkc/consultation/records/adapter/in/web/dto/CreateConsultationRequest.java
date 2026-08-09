package com.vkc.consultation.records.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.domain.model.ConsultationType;

public record CreateConsultationRequest(
        ConsultationType type,
        ConsultationStatus status,
        String consultantId,
        String consulteeId,
        String diagnosis,
        String prescription,
        String comments,
        Date consultationDate,
        Date followUpDate,
        String createdBy,
        BigDecimal fee) {}
