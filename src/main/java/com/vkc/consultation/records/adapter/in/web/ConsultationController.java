package com.vkc.consultation.records.adapter.in.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.server.ResponseStatusException;

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
import com.vkc.consultation.records.security.AuthenticatedUser;

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

    private Map<String, String> consulteeNamesById() {
        return consulteeUseCase.findConsultees().stream()
                .collect(Collectors.toMap(Consultee::getId, Consultee::getName, (a, b) -> a));
    }

    private boolean isConsultant(AuthenticatedUser user) {
        return user != null && "CONSULTANT".equals(user.role());
    }

    private void ensureConsultantCanAccessConsultantId(AuthenticatedUser user, String consultantId) {
        if (isConsultant(user) && (user.consultantId() == null || !user.consultantId().equals(consultantId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Consultants can only access their own sessions");
        }
    }

    private String requireConsultantId(AuthenticatedUser user) {
        if (isConsultant(user) && user.consultantId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Consultant account is not linked");
        }
        return user != null ? user.consultantId() : null;
    }

    private void ensureConsultantCanAccessConsultation(AuthenticatedUser user, String consultationId) {
        if (!isConsultant(user)) {
            return;
        }
        String currentConsultantId = user.consultantId();
        if (currentConsultantId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Consultant account is not linked");
        }
        String consultationConsultantId = consultationUseCase.findConsultationById(consultationId).getConsultantId();
        if (!currentConsultantId.equals(consultationConsultantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Consultants can only access their own sessions");
        }
    }

    @GetMapping(path = "/consultations")
    @ResponseBody
    public List<ConsultationResponse> fetchConsultations(@AuthenticationPrincipal AuthenticatedUser user) {
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> consulteeNames = consulteeNamesById();
        List<com.vkc.consultation.records.application.domain.model.Consultation> consultations = isConsultant(user)
            ? consultationUseCase.findConsultationByConsultant(requireConsultantId(user))
                : consultationUseCase.findConsultations();
        return consultations.stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, consulteeNames))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public ConsultationResponse findConsultation(@PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        ensureConsultantCanAccessConsultation(user, id);
        return ConsultationResponse.from(consultationUseCase.findConsultationById(id), consultantNamesById(),
                consulteeNamesById());
    }

    @GetMapping(path = "/consultations/consultant/{consultantId}")
    @ResponseBody
    public List<ConsultationResponse> findConsultationByConsultant(@PathVariable String consultantId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        ensureConsultantCanAccessConsultantId(user, consultantId);
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> consulteeNames = consulteeNamesById();
        return consultationUseCase.findConsultationByConsultant(consultantId).stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, consulteeNames))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/consultations/consultee/{consulteeId}")
    @ResponseBody
    public List<ConsultationResponse> findConsultationByPatient(@PathVariable String consulteeId) {
        Map<String, String> consultantNames = consultantNamesById();
        Map<String, String> consulteeNames = consulteeNamesById();
        return consultationUseCase.findConsultationByConsultee(consulteeId).stream()
                .map(c -> ConsultationResponse.from(c, consultantNames, consulteeNames))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/consultations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ConsultationResponse createConsultation(@RequestBody CreateConsultationRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        ensureConsultantCanAccessConsultantId(user, request.consultantId());
        CreateConsultationCommand command = new CreateConsultationCommand(
                request.type(), request.status(), request.consultantId(), request.consulteeId(),
            request.diagnosis(), request.prescription(), request.comments(), request.rating(), request.feedback(),
                request.consultationDate(), request.followUpDate(), request.createdBy(), request.fee());
        return ConsultationResponse.from(consultationUseCase.createConsultation(command), consultantNamesById(),
                consulteeNamesById());
    }

    @PutMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public ConsultationResponse updateConsultation(@PathVariable String id,
            @RequestBody UpdateConsultationRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        ensureConsultantCanAccessConsultation(user, id);
        ensureConsultantCanAccessConsultantId(user, request.consultantId());
        UpdateConsultationCommand command = new UpdateConsultationCommand(
                request.type(), request.status(), request.consultantId(), request.consulteeId(),
            request.diagnosis(), request.prescription(), request.comments(), request.rating(), request.feedback(),
                request.consultationDate(), request.followUpDate(), request.updatedDate(),
                request.createdBy(), request.fee());
        return ConsultationResponse.from(consultationUseCase.updateConsultation(id, command), consultantNamesById(),
                consulteeNamesById());
    }

    @DeleteMapping(path = "/consultations/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsultation(@PathVariable String id) {
        consultationUseCase.deleteConsultation(id);
    }
}
