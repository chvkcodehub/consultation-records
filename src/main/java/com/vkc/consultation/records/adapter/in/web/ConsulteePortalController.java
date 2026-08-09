package com.vkc.consultation.records.adapter.in.web;

import java.util.List;
import java.util.Map;
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
import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.port.in.BookConsultationCommand;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
import com.vkc.consultation.records.application.port.in.ConsulteeUseCase;
import com.vkc.consultation.records.security.AuthenticatedUser;

@RestController
@RequestMapping("/portal")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsulteePortalController {

    private final ConsultationUseCase consultationUseCase;
    private final ConsulteeUseCase consulteeUseCase;
    private final ConsultantUseCase consultantUseCase;

    public ConsulteePortalController(ConsultationUseCase consultationUseCase, ConsulteeUseCase consulteeUseCase,
            ConsultantUseCase consultantUseCase) {
        this.consultationUseCase = consultationUseCase;
        this.consulteeUseCase = consulteeUseCase;
        this.consultantUseCase = consultantUseCase;
    }

    private Map<String, String> consultantNamesById() {
        return consultantUseCase.findConsultants().stream()
                .collect(Collectors.toMap(Consultant::getId, Consultant::getName, (a, b) -> a));
    }

    private Map<String, String> ownNameById(String consulteeId) {
        Consultee consultee = consulteeUseCase.findConsulteeById(consulteeId);
        return Map.of(consulteeId, consultee.getName());
    }

    @PostMapping(path = "/consultations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ConsultationResponse bookConsultation(@AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody BookConsultationRequest request) {
        BookConsultationCommand command = new BookConsultationCommand(
                user.consulteeId(), request.consultantId(), request.type(),
                request.consultationDate(), request.comments());
        return ConsultationResponse.from(consultationUseCase.bookConsultation(command), consultantNamesById(),
                ownNameById(user.consulteeId()));
    }

    @GetMapping(path = "/consultations")
    @ResponseBody
    public List<ConsultationResponse> myConsultations(@AuthenticationPrincipal AuthenticatedUser user) {
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> consulteeNames = ownNameById(user.consulteeId());
        return consultationUseCase.findConsultationByConsultee(user.consulteeId()).stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, consulteeNames))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/{id}")
    @ResponseBody
    public ConsultationResponse myConsultationDetail(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String id) {
        return ConsultationResponse.from(
                consultationUseCase.findConsultationForPatient(id, user.consulteeId()), consultantNamesById(),
                ownNameById(user.consulteeId()));
    }

    @GetMapping(path = "/me")
    @ResponseBody
    public ConsulteeResponse myProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ConsulteeResponse.from(consulteeUseCase.findConsulteeById(user.consulteeId()));
    }
}
