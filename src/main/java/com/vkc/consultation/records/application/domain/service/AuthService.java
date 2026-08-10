package com.vkc.consultation.records.application.domain.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.domain.model.Role;
import com.vkc.consultation.records.application.domain.model.User;
import com.vkc.consultation.records.application.port.in.AuthResult;
import com.vkc.consultation.records.application.port.in.AuthUseCase;
import com.vkc.consultation.records.application.port.in.RegisterConsulteeCommand;
import com.vkc.consultation.records.application.port.out.ConsulteePort;
import com.vkc.consultation.records.application.port.out.TokenPort;
import com.vkc.consultation.records.application.port.out.UserPort;

@Service
public class AuthService implements AuthUseCase {

    private final UserPort userPort;
    private final ConsulteePort consulteePort;
    private final PasswordEncoder passwordEncoder;
    private final TokenPort tokenPort;

    public AuthService(UserPort userPort, ConsulteePort consulteePort, PasswordEncoder passwordEncoder,
            TokenPort tokenPort) {
        this.userPort = userPort;
        this.consulteePort = consulteePort;
        this.passwordEncoder = passwordEncoder;
        this.tokenPort = tokenPort;
    }

    @Override
    public AuthResult register(String email, String password) {
        if (userPort.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(Role.ADMIN);
        user.setPasswordChangeRequired(false);
        user.setCreatedAt(Instant.now());
        userPort.save(user);
        String token = tokenPort.generateToken(email, Role.ADMIN.name(), null, null);
        return new AuthResult(token, Role.ADMIN.name(), null, null, false);
    }

    @Override
    public AuthResult login(String email, String password) {
        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String role = user.getRole().name();
        String token = tokenPort.generateToken(email, role, user.getConsulteeId(), user.getConsultantId());
        return new AuthResult(token, role, user.getConsulteeId(), user.getConsultantId(),
            user.isPasswordChangeRequired());
    }

    @Override
    public AuthResult registerConsultee(RegisterConsulteeCommand command) {
        if (userPort.existsByEmail(command.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String consulteeId = consulteePort.findByEmail(command.email())
                .map(Consultee::getId)
                .orElseGet(() -> createConsultee(command));

        User user = new User();
        user.setEmail(command.email());
        user.setPasswordHash(passwordEncoder.encode(command.password()));
        user.setRole(Role.CONSULTEE);
        user.setConsulteeId(consulteeId);
        user.setPasswordChangeRequired(false);
        user.setCreatedAt(Instant.now());
        userPort.save(user);

        String token = tokenPort.generateToken(command.email(), Role.CONSULTEE.name(), consulteeId, null);
        return new AuthResult(token, Role.CONSULTEE.name(), consulteeId, null, false);
    }

    private String createConsultee(RegisterConsulteeCommand command) {
        Consultee consultee = new Consultee();
        consultee.setName(command.name());
        consultee.setGender(command.gender());
        consultee.setDob(command.dob());
        consultee.setAddress(command.address());
        consultee.setEmail(command.email());
        consultee.setPhone(command.phone());
        consultee.setStartDate(new Date());
        consultee.setRecoveryStatus("Not Started");
        return consulteePort.save(consultee).getId();
    }

    @Override
    public void forgotPassword(String email) {
        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found"));
        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(Instant.now().plusSeconds(3600));
        userPort.save(user);
        // In production: deliver reset token via email to the user
    }

    @Override
    public void resetPassword(String email, String resetToken, String newPassword) {
        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found"));
        if (user.getResetToken() == null || !user.getResetToken().equals(resetToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reset token");
        }
        if (user.getResetTokenExpiry() == null || Instant.now().isAfter(user.getResetTokenExpiry())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has expired");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userPort.save(user);
    }

    @Override
    public void changePassword(String email, String consultantId, String currentPassword, String newPassword) {
        User user = resolveUserForCredentialUpdate(email, consultantId);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        userPort.save(user);
    }

    @Override
    public void changeEmail(String currentEmail, String consultantId, String newEmail, String currentPassword) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New email is required");
        }

        User user = resolveUserForCredentialUpdate(currentEmail, consultantId);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        boolean emailChanged = !newEmail.trim().equalsIgnoreCase(currentEmail.trim());
        if (emailChanged && userPort.existsByEmail(newEmail.trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        user.setEmail(newEmail.trim());
        userPort.save(user);
    }

    private User resolveUserForCredentialUpdate(String email, String consultantId) {
        if (consultantId != null && !consultantId.isBlank()) {
            return userPort.findByConsultantId(consultantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        }

        return userPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
