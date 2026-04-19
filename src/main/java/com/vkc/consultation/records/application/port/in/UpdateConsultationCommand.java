package com.vkc.consultation.records.application.port.in;

import java.math.BigDecimal;
import java.util.Date;

public record UpdateConsultationCommand(
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
        BigDecimal fee) {}
