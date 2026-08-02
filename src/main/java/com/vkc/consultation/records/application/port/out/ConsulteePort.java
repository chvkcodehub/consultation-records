package com.vkc.consultation.records.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultee;

public interface ConsulteePort {
    List<Consultee> findAll();
    Optional<Consultee> findById(@NonNull String id);
    Optional<Consultee> findByEmail(String email);
    Consultee save(@NonNull Consultee consultee);
    boolean existsById(@NonNull String id);
    void deleteById(@NonNull String id);
}
