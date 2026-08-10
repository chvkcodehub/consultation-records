package com.vkc.consultation.records.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.adapter.in.web.dto.ChangeEmailRequest;
import com.vkc.consultation.records.adapter.in.web.dto.ChangePasswordRequest;
import com.vkc.consultation.records.adapter.in.web.dto.ConsultantProfileResponse;
import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.port.in.AuthUseCase;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.out.ConsultantPort;
import com.vkc.consultation.records.security.AuthenticatedUser;

@RestController
@RequestMapping("/consultant-portal")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsultantPortalController {

    private final ConsultantUseCase consultantUseCase;
    private final ConsultantPort consultantPort;
    private final AuthUseCase authUseCase;

    public ConsultantPortalController(ConsultantUseCase consultantUseCase, ConsultantPort consultantPort,
            AuthUseCase authUseCase) {
        this.consultantUseCase = consultantUseCase;
        this.consultantPort = consultantPort;
        this.authUseCase = authUseCase;
    }

    @GetMapping(path = "/me")
    @ResponseBody
    public ConsultantProfileResponse myProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        String consultantId = requireConsultantId(user);
        return ConsultantProfileResponse.from(consultantUseCase.findConsultantById(consultantId));
    }

    @PostMapping(path = "/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody ChangePasswordRequest request) {
        String consultantId = requireConsultantId(user);
        authUseCase.changePassword(user.email(), consultantId, request.currentPassword(), request.newPassword());
    }

    @PostMapping(path = "/change-email")
    @ResponseBody
    public ConsultantProfileResponse changeEmail(@AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody ChangeEmailRequest request) {
        String consultantId = requireConsultantId(user);
        authUseCase.changeEmail(user.email(), consultantId, request.newEmail(), request.currentPassword());

        Consultant consultant = consultantUseCase.findConsultantById(consultantId);
        consultant.setEmail(request.newEmail().trim());
        Consultant updated = consultantPort.save(consultant);
        return ConsultantProfileResponse.from(updated);
    }

    private String requireConsultantId(AuthenticatedUser user) {
        if (user == null || user.consultantId() == null || user.consultantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Consultant account is not linked");
        }
        return user.consultantId();
    }
}
