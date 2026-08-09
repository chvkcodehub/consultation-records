package com.vkc.consultation.records.application.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class ConsultantSummaryReport {
    private int totalConsultants;
    private long totalSessions;
    private List<ConsultantSummaryBreakdown> breakdown;
}
