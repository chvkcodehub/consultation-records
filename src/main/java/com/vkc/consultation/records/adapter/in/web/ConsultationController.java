package com.vkc.consultation.records.adapter.in.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.ConsultationResponse;
import com.vkc.consultation.records.adapter.in.web.dto.CreateConsultationRequest;
import com.vkc.consultation.records.adapter.in.web.dto.UpdateConsultationRequest;
import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.in.ConsulteeUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultationCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultationCommand;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsultationController {

    private final ConsultationUseCase consultationUseCase;
    private final ConsultantUseCase consultantUseCase;
    private final ConsulteeUseCase consulteeUseCase;

    public ConsultationController(ConsultationUseCase consultationUseCase, ConsultantUseCase consultantUseCase,
            ConsulteeUseCase consulteeUseCase) {
        this.consultationUseCase = consultationUseCase;
        this.consultantUseCase = consultantUseCase;
        this.consulteeUseCase = consulteeUseCase;
    }

    private Map<String, String> consultantNamesById() {
        return consultantUseCase.findConsultants().stream()
                .collect(Collectors.toMap(Consultant::getId, Consultant::getName, (a, b) -> a));
    }

    private Map<String, String> patientNamesById() {
        return consulteeUseCase.findConsultees().stream()
                .collect(Collectors.toMap(Consultee::getId, Consultee::getName, (a, b) -> a));
    }

    @GetMapping(path = "/consultations")
    @ResponseBody
    public List<ConsultationResponse> fetchConsultations() {
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> patientNames = patientNamesById();
        return consultationUseCase.findConsultations().stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, patientNames))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public ConsultationResponse findConsultation(@PathVariable String id) {
        return ConsultationResponse.from(consultationUseCase.findConsultationById(id), consultantNamesById(),
                patientNamesById());
    }

    @GetMapping(path = "/consultations/consultant/{consultantId}")
    @ResponseBody
    public List<ConsultationResponse> findConsultationByConsultant(@PathVariable String consultantId) {
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> patientNames = patientNamesById();
        return consultationUseCase.findConsultationByConsultant(consultantId).stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, patientNames))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/patient/{patientId}")
    @ResponseBody
    public List<ConsultationResponse> findConsultationByPatient(@PathVariable String patientId) {
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> patientNames = patientNamesById();
        return consultationUseCase.findConsultationByPatient(patientId).stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, patientNames))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/consultations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ConsultationResponse createConsultation(@RequestBody CreateConsultationRequest request) {
        CreateConsultationCommand command = new CreateConsultationCommand(
                request.type(), request.status(), request.consultantId(), request.patientId(),
                request.diagnosis(), request.prescription(), request.comments(),
                request.consultationDate(), request.followUpDate(), request.createdBy(), request.fee());
        return ConsultationResponse.from(consultationUseCase.createConsultation(command), consultantNamesById(),
                patientNamesById());
    }

    @PutMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public ConsultationResponse updateConsultation(@PathVariable String id,
            @RequestBody UpdateConsultationRequest request) {
        UpdateConsultationCommand command = new UpdateConsultationCommand(
                request.type(), request.status(), request.consultantId(), request.patientId(),
                request.diagnosis(), request.prescription(), request.comments(),
                request.consultationDate(), request.followUpDate(), request.updatedDate(),
                request.createdBy(), request.fee());
        return ConsultationResponse.from(consultationUseCase.updateConsultation(id, command), consultantNamesById(),
                patientNamesById());
    }

    @DeleteMapping(path = "/consultations/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsultation(@PathVariable String id) {
        consultationUseCase.deleteConsultation(id);
    }
}
