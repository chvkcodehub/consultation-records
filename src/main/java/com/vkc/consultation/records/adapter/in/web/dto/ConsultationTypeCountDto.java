package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.domain.model.ConsultationType;
import com.vkc.consultation.records.application.domain.model.ConsultationTypeCount;

public record ConsultationTypeCountDto(ConsultationType type, long count) {

    public static ConsultationTypeCountDto from(ConsultationTypeCount c) {
        return new ConsultationTypeCountDto(c.getType(), c.getCount());
    }
}
