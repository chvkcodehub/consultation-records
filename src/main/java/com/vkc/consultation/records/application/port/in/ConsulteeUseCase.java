package com.vkc.consultation.records.application.port.in;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultee;

public interface ConsulteeUseCase {
    List<Consultee> findConsultees();
    Consultee findConsulteeById(@NonNull String id);
    Consultee createConsultee(@NonNull CreateConsulteeCommand command);
    Consultee updateConsultee(@NonNull String id, @NonNull UpdateConsulteeCommand command);
    void deleteConsultee(@NonNull String id);
}
