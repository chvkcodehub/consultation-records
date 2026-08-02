package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.vkc.consultation.records.application.domain.model.ConsultantSummaryReport;

public record ConsultantSummaryReportResponse(
        int totalConsultants,
        long totalSessions,
        List<ConsultantSummaryBreakdownDto> breakdown) {

    public static ConsultantSummaryReportResponse from(ConsultantSummaryReport report) {
        return new ConsultantSummaryReportResponse(
                report.getTotalConsultants(),
                report.getTotalSessions(),
                report.getBreakdown().stream()
                        .map(ConsultantSummaryBreakdownDto::from)
                        .collect(Collectors.toList()));
    }
}
