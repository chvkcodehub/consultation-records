package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.Date;

import com.vkc.consultation.records.application.domain.model.ConsultationType;

public record BookConsultationRequest(
        String consultantId,
        ConsultationType type,
        Date consultationDate,
        String comments) {}
