package com.vkc.consultation.records.application.domain.service;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.adapter.out.persistence.ConsultationAdapter;
import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
@Service
public class ConsultationService implements ConsultationUseCase {
private final ConsultationAdapter consultationAdapter;

    public ConsultationService(ConsultationAdapter consultationAdapter) {
        this.consultationAdapter = consultationAdapter;
    }

     @Override
    public List<Consultation> findConsultations() {
        return consultationAdapter.findConsultations();
    }

    @Override
    public Consultation findConsultationById(@NonNull String id) {
        return consultationAdapter.findConsultationById(id);
    }

    @Override
    public Consultation findConsultationByCode(String code) {
        return consultationAdapter.findConsultationByCode(code);
    }

    @Override
    public List<Consultation> findConsultationByConsultant(String consultantCode) {
        return consultationAdapter.findConsultationsByConsultant(consultantCode);
    }

    @Override
    public List<Consultation> findConsultationByPatient(String patientCode) {
        return consultationAdapter.findConsultationsByPatient(patientCode);
    }

    @Override
    public Consultation createConsultation(@NonNull Consultation consultation) {
        setConsultationId(consultation, null);
        return consultationAdapter.saveConsultation(consultation);
    }

    @Override
    public Consultation updateConsultation(@NonNull String id, @NonNull Consultation consultation) {
        if (!consultationAdapter.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found with id: " + id);
        }
        setConsultationId(consultation, id);
        return consultationAdapter.saveConsultation(consultation);
    }

    @Override
    public void deleteConsultation(@NonNull String id) {
        if (!consultationAdapter.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found with id: " + id);
        }
        consultationAdapter.deleteById(id);
    }

    private void setConsultationId(@NonNull Consultation consultation, String id) {
        try {
            Field idField = Consultation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(consultation, id);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to set consultation id", ex);
        }
    }

}
