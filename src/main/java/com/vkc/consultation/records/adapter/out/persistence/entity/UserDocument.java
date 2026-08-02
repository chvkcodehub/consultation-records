package com.vkc.consultation.records.adapter.out.persistence.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserDocument {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private String role;
    private String consulteeId;
    private Instant createdAt;
    private String resetToken;
    private Instant resetTokenExpiry;
}
