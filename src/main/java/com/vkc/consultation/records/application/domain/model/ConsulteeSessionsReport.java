package com.vkc.consultation.records.application.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class ConsulteeSessionsReport {
    private long totalSessions;
    private List<ConsulteeSessionBreakdown> breakdown;
}
