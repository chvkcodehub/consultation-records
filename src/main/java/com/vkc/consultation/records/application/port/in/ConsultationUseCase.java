package com.vkc.consultation.records.application.port.in;

import java.util.List;

import org.springframework.lang.NonNull;

import com.vkc.consultation.records.application.domain.model.Consultation;

public interface ConsultationUseCase {
    List<Consultation> findConsultations();

    Consultation findConsultationById(@NonNull String id);

    List<Consultation> findConsultationByConsultant(String consultantId);

    List<Consultation> findConsultationByPatient(String patientId);

    Consultation createConsultation(@NonNull CreateConsultationCommand command);

    Consultation updateConsultation(@NonNull String id, @NonNull UpdateConsultationCommand command);

    void deleteConsultation(@NonNull String id);

    Consultation bookConsultation(@NonNull BookConsultationCommand command);

    Consultation findConsultationForPatient(@NonNull String id, @NonNull String patientId);
}
