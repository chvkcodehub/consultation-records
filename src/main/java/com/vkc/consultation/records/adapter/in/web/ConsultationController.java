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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.ConsultationResponse;
import com.vkc.consultation.records.adapter.in.web.dto.CreateConsultationRequest;
import com.vkc.consultation.records.adapter.in.web.dto.UpdateConsultationRequest;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultationCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultationCommand;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsultationController {

    private final ConsultationUseCase consultationUseCase;

    public ConsultationController(ConsultationUseCase consultationUseCase) {
        this.consultationUseCase = consultationUseCase;
    }

    @GetMapping(path = "/consultations")
    @ResponseBody
    public List<ConsultationResponse> fetchConsultations() {
        return consultationUseCase.findConsultations().stream()
                .map(ConsultationResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public ConsultationResponse findConsultation(@PathVariable String id) {
        return ConsultationResponse.from(consultationUseCase.findConsultationById(id));
    }

    @GetMapping(path = "/consultations/code/{code}")
    @ResponseBody
    public ConsultationResponse findConsultationByCode(@PathVariable String code) {
        return ConsultationResponse.from(consultationUseCase.findConsultationByCode(code));
    }

    @GetMapping(path = "/consultations/consultant/{consultantCode}")
    @ResponseBody
    public List<ConsultationResponse> findConsultationByConsultant(@PathVariable String consultantCode) {
        return consultationUseCase.findConsultationByConsultant(consultantCode).stream()
                .map(ConsultationResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/patient/{patientCode}")
    @ResponseBody
    public List<ConsultationResponse> findConsultationByPatient(@PathVariable String patientCode) {
        return consultationUseCase.findConsultationByPatient(patientCode).stream()
                .map(ConsultationResponse::from)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/consultations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ConsultationResponse createConsultation(@RequestBody CreateConsultationRequest request) {
        CreateConsultationCommand command = new CreateConsultationCommand(
                request.code(), request.type(), request.consultantCode(), request.patientCode(),
                request.diagnosis(), request.prescription(), request.comments(),
                request.consultationDate(), request.followUpDate(), request.createdBy(), request.fee());
        return ConsultationResponse.from(consultationUseCase.createConsultation(command));
    }

    @PutMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public ConsultationResponse updateConsultation(@PathVariable String id,
            @RequestBody UpdateConsultationRequest request) {
        UpdateConsultationCommand command = new UpdateConsultationCommand(
                request.code(), request.type(), request.consultantCode(), request.patientCode(),
                request.diagnosis(), request.prescription(), request.comments(),
                request.consultationDate(), request.followUpDate(), request.updatedDate(),
                request.createdBy(), request.fee());
        return ConsultationResponse.from(consultationUseCase.updateConsultation(id, command));
    }

    @DeleteMapping(path = "/consultations/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsultation(@PathVariable String id) {
        consultationUseCase.deleteConsultation(id);
    }
}
