package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.vkc.consultation.records.application.domain.model.ConsulteeSessionsReport;

public record ConsulteeSessionsReportResponse(long totalSessions, List<ConsulteeSessionBreakdownDto> breakdown) {

    public static ConsulteeSessionsReportResponse from(ConsulteeSessionsReport report) {
        return new ConsulteeSessionsReportResponse(
                report.getTotalSessions(),
                report.getBreakdown().stream()
                        .map(ConsulteeSessionBreakdownDto::from)
                        .collect(Collectors.toList()));
    }
}
