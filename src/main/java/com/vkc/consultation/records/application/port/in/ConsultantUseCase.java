package com.vkc.consultation.records.application.port.in;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultant;

public interface ConsultantUseCase {
    List<Consultant> findConsultants();
    Consultant findConsultantById(@NonNull String id);
    Consultant findConsultantByCode(String code);
    Consultant createConsultant(@NonNull CreateConsultantCommand command);
    Consultant updateConsultant(@NonNull String id, @NonNull UpdateConsultantCommand command);
    void deleteConsultant(@NonNull String id);
}
