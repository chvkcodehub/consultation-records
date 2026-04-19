package com.vkc.consultation.records.application.domain.model;

import java.time.Instant;
import java.util.Set;

import lombok.Data;

@Data
public class User {
    private String id;
    private String email;
    private String passwordHash;
    private Set<String> roles;
    private Instant createdAt;
    private String resetToken;
    private Instant resetTokenExpiry;
}
