package com.vkc.consultation.records.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.Date;

public record CreateConsultationRequest(
        String code,
        String type,
        String consultantCode,
        String patientCode,
        String diagnosis,
        String prescription,
        String comments,
        Date consultationDate,
        Date followUpDate,
        String createdBy,
        BigDecimal fee) {}
