package com.vkc.consultation.records.application.domain.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.port.in.BookConsultationCommand;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultationCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultationCommand;
import com.vkc.consultation.records.application.port.out.ConsultantPort;
import com.vkc.consultation.records.application.port.out.ConsultationPort;

@Service
public class ConsultationService implements ConsultationUseCase {

    private final ConsultationPort consultationPort;
    private final ConsultantPort consultantPort;

    public ConsultationService(ConsultationPort consultationPort, ConsultantPort consultantPort) {
        this.consultationPort = consultationPort;
        this.consultantPort = consultantPort;
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
        consultation.setStatus(command.status() != null ? command.status() : ConsultationStatus.BOOKED);
        consultation.setConsultantCode(command.consultantCode());
        consultation.setPatientCode(command.patientCode());
        consultation.setDiagnosis(command.diagnosis());
        consultation.setPrescription(command.prescription());
        consultation.setComments(command.comments());
        consultation.setConsultationDate(command.consultationDate());
        consultation.setFollowUpDate(command.followUpDate());
        consultation.setCreatedBy(command.createdBy());
        consultation.setFee(command.fee());
        consultation.setUpdatedDate(new Date());
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
        consultation.setStatus(command.status());
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

    @Override
    public Consultation bookConsultation(@NonNull BookConsultationCommand command) {
        Consultant consultant = consultantPort.findByCode(command.consultantCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Consultant not found with code: " + command.consultantCode()));

        Consultation consultation = new Consultation();
        consultation.setCode("CN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        consultation.setType(command.type());
        consultation.setStatus(ConsultationStatus.BOOKED);
        consultation.setConsultantCode(command.consultantCode());
        consultation.setPatientCode(command.patientCode());
        consultation.setComments(command.comments());
        consultation.setConsultationDate(command.consultationDate());
        consultation.setFee(BigDecimal.valueOf(consultant.getFee()));
        consultation.setUpdatedDate(new Date());
        return consultationPort.saveConsultation(consultation);
    }

    @Override
    public Consultation findConsultationForPatient(@NonNull String id, @NonNull String patientCode) {
        Consultation consultation = consultationPort.findConsultationById(id);
        if (!patientCode.equals(consultation.getPatientCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this consultation");
        }
        return consultation;
    }

}
