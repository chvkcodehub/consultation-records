package com.vkc.consultation.records.application.port.in;

import java.math.BigDecimal;
import java.util.Date;

public record CreateConsultationCommand(
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
