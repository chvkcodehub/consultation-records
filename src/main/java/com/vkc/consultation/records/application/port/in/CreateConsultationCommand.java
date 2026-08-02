package com.vkc.consultation.records.application.port.in;

import java.math.BigDecimal;
import java.util.Date;

import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.domain.model.ConsultationType;

public record CreateConsultationCommand(
        ConsultationType type,
        ConsultationStatus status,
        String consultantId,
        String patientId,
        String diagnosis,
        String prescription,
        String comments,
        Date consultationDate,
        Date followUpDate,
        String createdBy,
        BigDecimal fee) {}
