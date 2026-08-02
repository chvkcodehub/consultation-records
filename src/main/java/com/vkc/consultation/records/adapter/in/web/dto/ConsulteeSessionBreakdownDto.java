package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.domain.model.ConsulteeSessionBreakdown;

public record ConsulteeSessionBreakdownDto(String consulteeId, String consulteeName, long sessionCount) {

    public static ConsulteeSessionBreakdownDto from(ConsulteeSessionBreakdown b) {
        return new ConsulteeSessionBreakdownDto(b.getConsulteeId(), b.getConsulteeName(), b.getSessionCount());
    }
}
