package com.vkc.consultation.records.application.domain.service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.User;
import com.vkc.consultation.records.application.port.in.AuthUseCase;
import com.vkc.consultation.records.application.port.out.UserPort;
import com.vkc.consultation.records.security.JwtUtil;

@Service
public class AuthService implements AuthUseCase {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserPort userPort, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String register(String email, String password) {
        if (userPort.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(Set.of("ROLE_USER"));
        user.setCreatedAt(Instant.now());
        userPort.save(user);
        return jwtUtil.generateToken(email);
    }

    @Override
    public String login(String email, String password) {
        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return jwtUtil.generateToken(email);
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
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userPort.save(user);
    }
}
