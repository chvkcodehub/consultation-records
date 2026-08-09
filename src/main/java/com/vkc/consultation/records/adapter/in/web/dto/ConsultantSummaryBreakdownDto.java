package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.vkc.consultation.records.application.domain.model.ConsultantSummaryBreakdown;

public record ConsultantSummaryBreakdownDto(
        String consultantId,
        String consultantName,
        long sessionCount,
        List<ConsultationTypeCountDto> byType) {

    public static ConsultantSummaryBreakdownDto from(ConsultantSummaryBreakdown b) {
        return new ConsultantSummaryBreakdownDto(
                b.getConsultantId(),
                b.getConsultantName(),
                b.getSessionCount(),
                b.getByType().stream()
                        .map(ConsultationTypeCountDto::from)
                        .collect(Collectors.toList()));
    }
}
