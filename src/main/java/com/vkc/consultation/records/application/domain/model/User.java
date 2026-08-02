package com.vkc.consultation.records.application.domain.model;

import java.time.Instant;

import lombok.Data;

@Data
public class User {
    private String id;
    private String email;
    private String passwordHash;
    private Role role;
    private String consulteeCode;
    private Instant createdAt;
    private String resetToken;
    private Instant resetTokenExpiry;
}
