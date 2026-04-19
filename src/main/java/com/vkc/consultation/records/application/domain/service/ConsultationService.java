package com.vkc.consultation.records.application.domain.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultationCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultationCommand;
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
    public Consultation createConsultation(@NonNull CreateConsultationCommand command) {
        Consultation consultation = new Consultation();
        consultation.setCode(command.code());
        consultation.setType(command.type());
        consultation.setConsultantCode(command.consultantCode());
        consultation.setPatientCode(command.patientCode());
        consultation.setDiagnosis(command.diagnosis());
        consultation.setPrescription(command.prescription());
        consultation.setComments(command.comments());
        consultation.setConsultationDate(command.consultationDate());
        consultation.setFollowUpDate(command.followUpDate());
        consultation.setCreatedBy(command.createdBy());
        consultation.setFee(command.fee());
        return consultationPort.saveConsultation(consultation);
    }

    @Override
    public Consultation updateConsultation(@NonNull String id, @NonNull UpdateConsultationCommand command) {
        if (!consultationPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found with id: " + id);
        }
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setCode(command.code());
        consultation.setType(command.type());
        consultation.setConsultantCode(command.consultantCode());
        consultation.setPatientCode(command.patientCode());
        consultation.setDiagnosis(command.diagnosis());
        consultation.setPrescription(command.prescription());
        consultation.setComments(command.comments());
        consultation.setConsultationDate(command.consultationDate());
        consultation.setFollowUpDate(command.followUpDate());
        consultation.setUpdatedDate(command.updatedDate());
        consultation.setCreatedBy(command.createdBy());
        consultation.setFee(command.fee());
        return consultationPort.saveConsultation(consultation);
    }

    @Override
    public void deleteConsultation(@NonNull String id) {
        if (!consultationPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found with id: " + id);
        }
        consultationPort.deleteById(id);
    }

}
