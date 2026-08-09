package com.vkc.consultation.records.application.domain.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultantCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultantCommand;
import com.vkc.consultation.records.application.port.out.ConsultantPort;

@Service
public class ConsultantService implements ConsultantUseCase {

    private final ConsultantPort consultantPort;

    public ConsultantService(ConsultantPort consultantPort) {
        this.consultantPort = consultantPort;
    }

    @Override
    public List<Consultant> findConsultants() {
        return consultantPort.findAll();
    }

    @Override
    public Consultant findConsultantById(@NonNull String id) {
        return consultantPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultant not found with id: " + id));
    }

    @Override
    public Consultant createConsultant(@NonNull CreateConsultantCommand command) {
        Consultant consultant = new Consultant();
        consultant.setName(command.name());
        consultant.setSpeciality(command.speciality());
        consultant.setQualification(command.qualification());
        consultant.setExperienceYears(command.experienceYears());
        consultant.setFee(command.fee());
        return consultantPort.save(consultant);
    }

    @Override
    public Consultant updateConsultant(@NonNull String id, @NonNull UpdateConsultantCommand command) {
        if (!consultantPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultant not found with id: " + id);
        }
        Consultant consultant = new Consultant();
        consultant.setId(id);
        consultant.setName(command.name());
        consultant.setSpeciality(command.speciality());
        consultant.setQualification(command.qualification());
        consultant.setExperienceYears(command.experienceYears());
        consultant.setFee(command.fee());
        return consultantPort.save(consultant);
    }

    @Override
    public void deleteConsultant(@NonNull String id) {
        if (!consultantPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultant not found with id: " + id);
        }
        consultantPort.deleteById(id);
    }
}
