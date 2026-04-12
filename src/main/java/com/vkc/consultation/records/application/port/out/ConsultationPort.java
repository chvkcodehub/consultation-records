package com.vkc.consultation.records.application.port.out;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultation;

public interface ConsultationPort {
    List<Consultation> findConsultations();
    Consultation findConsultationById(@NonNull String id);

    Consultation findConsultationByCode(String code);
    List<Consultation> findConsultationsByConsultant(String consultantCode);
    List<Consultation> findConsultationsByPatient(String patientCode);
    Consultation saveConsultation(@NonNull Consultation consultation);
    boolean existsById(@NonNull String id);
    void deleteById(@NonNull String id);

}
