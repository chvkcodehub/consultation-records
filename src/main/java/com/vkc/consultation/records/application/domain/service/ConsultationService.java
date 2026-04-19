package com.vkc.consultation.records.application.domain.service;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.out.ConsultationPort;
@Service
public class ConsultationService implements ConsultationUseCase {
private final ConsultationPort consultationPort;

    public ConsultationService(ConsultationPort consultationPort) {
        this.consultationPort = consultationPort;
    }

     @Override
    public List<Consultation> findConsultations() {
        return consultationPort.findConsultations();
    }

    @Override
    public Consultation findConsultationById(@NonNull String id) {
        return consultationPort.findConsultationById(id);
    }

    @Override
    public Consultation findConsultationByCode(String code) {
        return consultationPort.findConsultationByCode(code);
    }

    @Override
    public List<Consultation> findConsultationByConsultant(String consultantCode) {
        return consultationPort.findConsultationsByConsultant(consultantCode);
    }

    @Override
    public List<Consultation> findConsultationByPatient(String patientCode) {
        return consultationPort.findConsultationsByPatient(patientCode);
    }

    @Override
    public Consultation createConsultation(@NonNull Consultation consultation) {
        setConsultationId(consultation, null);
        return consultationPort.saveConsultation(consultation);
    }

    @Override
    public Consultation updateConsultation(@NonNull String id, @NonNull Consultation consultation) {
        if (!consultationPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found with id: " + id);
        }
        setConsultationId(consultation, id);
        return consultationPort.saveConsultation(consultation);
    }

    @Override
    public void deleteConsultation(@NonNull String id) {
        if (!consultationPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found with id: " + id);
        }
        consultationPort.deleteById(id);
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
