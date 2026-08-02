package com.vkc.consultation.records.adapter.in.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.BookConsultationRequest;
import com.vkc.consultation.records.adapter.in.web.dto.ConsulteeResponse;
import com.vkc.consultation.records.adapter.in.web.dto.ConsultationResponse;
import com.vkc.consultation.records.application.port.in.BookConsultationCommand;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.in.ConsulteeUseCase;
import com.vkc.consultation.records.security.AuthenticatedUser;

@RestController
@RequestMapping("/portal")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsulteePortalController {

    private final ConsultationUseCase consultationUseCase;
    private final ConsulteeUseCase consulteeUseCase;

    public ConsulteePortalController(ConsultationUseCase consultationUseCase, ConsulteeUseCase consulteeUseCase) {
        this.consultationUseCase = consultationUseCase;
        this.consulteeUseCase = consulteeUseCase;
    }

    @PostMapping(path = "/consultations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ConsultationResponse bookConsultation(@AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody BookConsultationRequest request) {
        BookConsultationCommand command = new BookConsultationCommand(
                user.consulteeCode(), request.consultantCode(), request.type(),
                request.consultationDate(), request.comments());
        return ConsultationResponse.from(consultationUseCase.bookConsultation(command));
    }

    @GetMapping(path = "/consultations")
    @ResponseBody
    public List<ConsultationResponse> myConsultations(@AuthenticationPrincipal AuthenticatedUser user) {
        return consultationUseCase.findConsultationByPatient(user.consulteeCode()).stream()
                .map(ConsultationResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/{id}")
    @ResponseBody
    public ConsultationResponse myConsultationDetail(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String id) {
        return ConsultationResponse.from(
                consultationUseCase.findConsultationForPatient(id, user.consulteeCode()));
    }

    @GetMapping(path = "/me")
    @ResponseBody
    public ConsulteeResponse myProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ConsulteeResponse.from(consulteeUseCase.findConsulteeByCode(user.consulteeCode()));
    }
}
