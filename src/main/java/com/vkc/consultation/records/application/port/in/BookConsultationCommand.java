package com.vkc.consultation.records.application.port.in;

import java.util.Date;

import com.vkc.consultation.records.application.domain.model.ConsultationType;

public record BookConsultationCommand(
        String patientId,
        String consultantId,
        ConsultationType type,
        Date consultationDate,
        String comments) {}
