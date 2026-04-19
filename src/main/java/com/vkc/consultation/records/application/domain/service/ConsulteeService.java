package com.vkc.consultation.records.application.domain.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.port.in.ConsulteeUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsulteeCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsulteeCommand;
import com.vkc.consultation.records.application.port.out.ConsulteePort;

@Service
public class ConsulteeService implements ConsulteeUseCase {

    private final ConsulteePort consulteePort;

    public ConsulteeService(ConsulteePort consulteePort) {
        this.consulteePort = consulteePort;
    }

    @Override
    public List<Consultee> findConsultees() {
        return consulteePort.findAll();
    }

    @Override
    public Consultee findConsulteeById(@NonNull String id) {
        return consulteePort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultee not found with id: " + id));
    }

    @Override
    public Consultee findConsulteeByCode(String code) {
        return consulteePort.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultee not found with code: " + code));
    }

    @Override
    public Consultee createConsultee(@NonNull CreateConsulteeCommand command) {
        Consultee consultee = new Consultee();
        consultee.setCode(command.code());
        consultee.setName(command.name());
        consultee.setGender(command.gender());
        consultee.setDob(command.dob());
        consultee.setCondition(command.condition());
        consultee.setAddress(command.address());
        consultee.setPhone(command.phone());
        consultee.setEmail(command.email());
        consultee.setStartDate(command.startDate());
        return consulteePort.save(consultee);
    }

    @Override
    public Consultee updateConsultee(@NonNull String id, @NonNull UpdateConsulteeCommand command) {
        if (!consulteePort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultee not found with id: " + id);
        }
        Consultee consultee = new Consultee();
        consultee.setId(id);
        consultee.setCode(command.code());
        consultee.setName(command.name());
        consultee.setGender(command.gender());
        consultee.setDob(command.dob());
        consultee.setCondition(command.condition());
        consultee.setAddress(command.address());
        consultee.setPhone(command.phone());
        consultee.setEmail(command.email());
        consultee.setStartDate(command.startDate());
        return consulteePort.save(consultee);
    }

    @Override
    public void deleteConsultee(@NonNull String id) {
        if (!consulteePort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultee not found with id: " + id);
        }
        consulteePort.deleteById(id);
    }
}
