package com.vkc.consultation.records.application.domain.model;

import lombok.Data;

@Data
public class ConsulteeSessionBreakdown {
    private String consulteeCode;
    private String consulteeName;
    private long sessionCount;
}
