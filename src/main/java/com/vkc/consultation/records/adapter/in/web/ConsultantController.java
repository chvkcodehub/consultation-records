package com.vkc.consultation.records.adapter.in.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.ConsultantResponse;
import com.vkc.consultation.records.adapter.in.web.dto.CreateConsultantRequest;
import com.vkc.consultation.records.adapter.in.web.dto.UpdateConsultantRequest;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultantCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultantCommand;

@RestController
@RequestMapping("/consultants")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsultantController {

    private final ConsultantUseCase consultantUseCase;

    public ConsultantController(ConsultantUseCase consultantUseCase) {
        this.consultantUseCase = consultantUseCase;
    }

    @GetMapping
    @ResponseBody
    public List<ConsultantResponse> fetchConsultants() {
        return consultantUseCase.findConsultants().stream()
                .map(ConsultantResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/id/{id}")
    @ResponseBody
    public ConsultantResponse findConsultantById(@PathVariable String id) {
        return ConsultantResponse.from(consultantUseCase.findConsultantById(id));
    }

    @GetMapping("/code/{code}")
    @ResponseBody
    public ConsultantResponse findConsultantByCode(@PathVariable String code) {
        return ConsultantResponse.from(consultantUseCase.findConsultantByCode(code));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ConsultantResponse createConsultant(@RequestBody CreateConsultantRequest request) {
        CreateConsultantCommand command = new CreateConsultantCommand(
                request.code(), request.name(), request.speciality(),
                request.qualification(), request.experienceYears(), request.fee());
        return ConsultantResponse.from(consultantUseCase.createConsultant(command));
    }

    @PutMapping("/id/{id}")
    @ResponseBody
    public ConsultantResponse updateConsultant(@PathVariable String id,
            @RequestBody UpdateConsultantRequest request) {
        UpdateConsultantCommand command = new UpdateConsultantCommand(
                request.code(), request.name(), request.speciality(),
                request.qualification(), request.experienceYears(), request.fee());
        return ConsultantResponse.from(consultantUseCase.updateConsultant(id, command));
    }

    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsultant(@PathVariable String id) {
        consultantUseCase.deleteConsultant(id);
    }
}
