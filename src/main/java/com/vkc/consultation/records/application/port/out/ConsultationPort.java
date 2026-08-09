package com.vkc.consultation.records.application.port.out;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultation;

public interface ConsultationPort {
    List<Consultation> findConsultations();
    Consultation findConsultationById(@NonNull String id);

    List<Consultation> findConsultationsByConsultant(String consultantId);
    List<Consultation> findConsultationsByConsultee(String consulteeId);
    Consultation saveConsultation(@NonNull Consultation consultation);
    boolean existsById(@NonNull String id);
    void deleteById(@NonNull String id);

}
