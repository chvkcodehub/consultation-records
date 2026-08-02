package com.vkc.consultation.records.adapter.out.persistence.mapper;

import com.vkc.consultation.records.adapter.out.persistence.entity.UserDocument;
import com.vkc.consultation.records.application.domain.model.Role;
import com.vkc.consultation.records.application.domain.model.User;

public class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserDocument doc) {
        User user = new User();
        user.setId(doc.getId());
        user.setEmail(doc.getEmail());
        user.setPasswordHash(doc.getPasswordHash());
        user.setRole(doc.getRole() != null ? Role.valueOf(doc.getRole()) : null);
        user.setConsulteeCode(doc.getConsulteeCode());
        user.setCreatedAt(doc.getCreatedAt());
        user.setResetToken(doc.getResetToken());
        user.setResetTokenExpiry(doc.getResetTokenExpiry());
        return user;
    }

    public static UserDocument toDocument(User user) {
        UserDocument doc = new UserDocument();
        doc.setId(user.getId());
        doc.setEmail(user.getEmail());
        doc.setPasswordHash(user.getPasswordHash());
        doc.setRole(user.getRole() != null ? user.getRole().name() : null);
        doc.setConsulteeCode(user.getConsulteeCode());
        doc.setCreatedAt(user.getCreatedAt());
        doc.setResetToken(user.getResetToken());
        doc.setResetTokenExpiry(user.getResetTokenExpiry());
        return doc;
    }
}
