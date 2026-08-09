package com.vkc.consultation.records.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultant;

public interface ConsultantPort {
    List<Consultant> findAll();
    Optional<Consultant> findById(@NonNull String id);
    Consultant save(@NonNull Consultant consultant);
    boolean existsById(@NonNull String id);
    void deleteById(@NonNull String id);
}
