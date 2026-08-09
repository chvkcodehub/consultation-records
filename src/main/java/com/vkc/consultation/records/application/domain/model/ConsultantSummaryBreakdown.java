package com.vkc.consultation.records.application.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class ConsultantSummaryBreakdown {
    private String consultantId;
    private String consultantName;
    private long sessionCount;
    private List<ConsultationTypeCount> byType;
}
