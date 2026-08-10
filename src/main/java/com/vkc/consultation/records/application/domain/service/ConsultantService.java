package com.vkc.consultation.records.application.domain.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.Role;
import com.vkc.consultation.records.application.domain.model.User;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultantCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultantCommand;
import com.vkc.consultation.records.application.port.out.ConsultantPort;
import com.vkc.consultation.records.application.port.out.EmailPort;
import com.vkc.consultation.records.application.port.out.UserPort;

@Service
public class ConsultantService implements ConsultantUseCase {

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private final ConsultantPort consultantPort;
    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final EmailPort emailPort;
    private final SecureRandom secureRandom = new SecureRandom();

    public ConsultantService(ConsultantPort consultantPort, UserPort userPort, PasswordEncoder passwordEncoder,
            EmailPort emailPort) {
        this.consultantPort = consultantPort;
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
    }

    @Override
    public List<Consultant> findConsultants() {
        return consultantPort.findAll();
    }

    @Override
    public Consultant findConsultantById(@NonNull String id) {
        return consultantPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultant not found with id: " + id));
    }

    @Override
    public Consultant createConsultant(@NonNull CreateConsultantCommand command) {
        if (command.email() == null || command.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Consultant email is required");
        }
        if (userPort.existsByEmail(command.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        Consultant consultant = new Consultant();
        consultant.setName(command.name());
        consultant.setEmail(command.email());
        consultant.setMobile(command.mobile());
        consultant.setSpeciality(command.speciality());
        consultant.setQualification(command.qualification());
        consultant.setExperienceYears(command.experienceYears());
        consultant.setFee(command.fee());
        Consultant saved = consultantPort.save(consultant);

        String temporaryPassword = generateTemporaryPassword();
        User user = new User();
        user.setEmail(command.email());
        user.setRole(Role.CONSULTANT);
        user.setConsultantId(saved.getId());
        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setPasswordChangeRequired(true);
        userPort.save(user);

        emailPort.sendConsultantWelcomeEmail(saved.getName(), saved.getEmail(), temporaryPassword);
        return saved;
    }

    @Override
    public Consultant updateConsultant(@NonNull String id, @NonNull UpdateConsultantCommand command) {
        if (!consultantPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultant not found with id: " + id);
        }

        Optional<User> existingUser = userPort.findByConsultantId(id);

        if (command.email() == null || command.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Consultant email is required");
        }

        String normalizedCurrentEmail = existingUser.map(User::getEmail).orElse("");
        normalizedCurrentEmail = normalizedCurrentEmail != null ? normalizedCurrentEmail.trim() : "";
        String normalizedNewEmail = command.email().trim();
        if (!normalizedCurrentEmail.equalsIgnoreCase(normalizedNewEmail) && userPort.existsByEmail(normalizedNewEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        Consultant consultant = new Consultant();
        consultant.setId(id);
        consultant.setName(command.name());
        consultant.setEmail(command.email());
        consultant.setMobile(command.mobile());
        consultant.setSpeciality(command.speciality());
        consultant.setQualification(command.qualification());
        consultant.setExperienceYears(command.experienceYears());
        consultant.setFee(command.fee());
        Consultant updated = consultantPort.save(consultant);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEmail(command.email());
            userPort.save(user);
        } else {
            String temporaryPassword = generateTemporaryPassword();
            User user = new User();
            user.setEmail(command.email());
            user.setRole(Role.CONSULTANT);
            user.setConsultantId(id);
            user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
            user.setPasswordChangeRequired(true);
            userPort.save(user);
            emailPort.sendConsultantWelcomeEmail(updated.getName(), updated.getEmail(), temporaryPassword);
        }
        return updated;
    }

    private String generateTemporaryPassword() {
        StringBuilder value = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int index = secureRandom.nextInt(TEMP_PASSWORD_CHARS.length());
            value.append(TEMP_PASSWORD_CHARS.charAt(index));
        }
        return value.toString();
    }

    @Override
    public void deleteConsultant(@NonNull String id) {
        if (!consultantPort.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultant not found with id: " + id);
        }
        consultantPort.deleteById(id);
    }
}
