package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.domain.model.ConsulteeSessionBreakdown;

public record ConsulteeSessionBreakdownDto(String consulteeCode, String consulteeName, long sessionCount) {

    public static ConsulteeSessionBreakdownDto from(ConsulteeSessionBreakdown b) {
        return new ConsulteeSessionBreakdownDto(b.getConsulteeCode(), b.getConsulteeName(), b.getSessionCount());
    }
}
