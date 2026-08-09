package com.vkc.consultation.records.application.domain.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    public List<Consultation> findConsultationByConsultant(String consultantId) {
        return consultationPort.findConsultationsByConsultant(consultantId);
    }

    @Override
    public List<Consultation> findConsultationByConsultee(String consulteeId) {
        return consultationPort.findConsultationsByConsultee(consulteeId);
    }

    @Override
    public Consultation createConsultation(@NonNull CreateConsultationCommand command) {
        Consultation consultation = new Consultation();
        consultation.setType(command.type());
        consultation.setStatus(command.status() != null ? command.status() : ConsultationStatus.BOOKED);
        consultation.setConsultantId(command.consultantId());
        consultation.setConsulteeId(command.consulteeId());
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
        consultation.setType(command.type());
        consultation.setStatus(command.status());
        consultation.setConsultantId(command.consultantId());
        consultation.setConsulteeId(command.consulteeId());
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
        Consultant consultant = consultantPort.findById(command.consultantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Consultant not found with id: " + command.consultantId()));

        Consultation consultation = new Consultation();
        consultation.setType(command.type());
        consultation.setStatus(ConsultationStatus.BOOKED);
        consultation.setConsultantId(command.consultantId());
        consultation.setConsulteeId(command.consulteeId());
        consultation.setComments(command.comments());
        consultation.setConsultationDate(command.consultationDate());
        consultation.setFee(BigDecimal.valueOf(consultant.getFee()));
        consultation.setUpdatedDate(new Date());
        return consultationPort.saveConsultation(consultation);
    }

    @Override
    public Consultation findConsultationForPatient(@NonNull String id, @NonNull String patientId) {
        Consultation consultation = consultationPort.findConsultationById(id);
        if (!patientId.equals(consultation.getConsulteeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this consultation");
        }
        return consultation;
    }

}
