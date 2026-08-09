package com.vkc.consultation.records.application.domain.model;

import lombok.Data;

@Data
public class ConsultationTypeCount {
    private ConsultationType type;
    private long count;
}
