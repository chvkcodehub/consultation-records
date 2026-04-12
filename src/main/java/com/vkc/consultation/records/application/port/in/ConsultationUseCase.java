package com.vkc.consultation.records.application.port.in;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultation;

public interface ConsultationUseCase {
    List<Consultation> findConsultations();

    Consultation findConsultationById(@NonNull String id);

    Consultation findConsultationByCode(String code);

    List<Consultation> findConsultationByConsultant(String consultantCode);

    List<Consultation> findConsultationByPatient(String patientCode);

    Consultation createConsultation(@NonNull Consultation consultation);

    Consultation updateConsultation(@NonNull String id, @NonNull Consultation consultation);

    void deleteConsultation(@NonNull String id);
}
