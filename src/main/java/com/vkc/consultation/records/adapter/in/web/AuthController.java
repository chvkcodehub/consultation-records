package com.vkc.consultation.records.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.AuthResponse;
import com.vkc.consultation.records.adapter.in.web.dto.ForgotPasswordRequest;
import com.vkc.consultation.records.adapter.in.web.dto.LoginRequest;
import com.vkc.consultation.records.adapter.in.web.dto.RegisterConsulteeRequest;
import com.vkc.consultation.records.adapter.in.web.dto.RegisterRequest;
import com.vkc.consultation.records.adapter.in.web.dto.ResetPasswordRequest;
import com.vkc.consultation.records.application.port.in.AuthUseCase;
import com.vkc.consultation.records.application.port.in.RegisterConsulteeCommand;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:8080"})
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return AuthResponse.from(authUseCase.register(request.email(), request.password()));
    }

    @PostMapping("/login")
    @ResponseBody
    public AuthResponse login(@RequestBody LoginRequest request) {
        return AuthResponse.from(authUseCase.login(request.email(), request.password()));
    }

    @PostMapping("/register-consultee")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AuthResponse registerConsultee(@RequestBody RegisterConsulteeRequest request) {
        RegisterConsulteeCommand command = new RegisterConsulteeCommand(
                request.email(), request.password(), request.name(), request.gender(),
                request.dob(), request.address(), request.phone());
        return AuthResponse.from(authUseCase.registerConsultee(command));
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authUseCase.forgotPassword(request.email());
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        authUseCase.resetPassword(request.email(), request.resetToken(), request.newPassword());
    }
}
