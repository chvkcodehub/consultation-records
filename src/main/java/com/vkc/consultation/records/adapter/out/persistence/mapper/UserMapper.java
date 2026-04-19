package com.vkc.consultation.records.adapter.out.persistence.mapper;

import com.vkc.consultation.records.adapter.out.persistence.entity.UserDocument;
import com.vkc.consultation.records.application.domain.model.User;

public class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserDocument doc) {
        User user = new User();
        user.setId(doc.getId());
        user.setEmail(doc.getEmail());
        user.setPasswordHash(doc.getPasswordHash());
        user.setRoles(doc.getRoles());
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
        doc.setRoles(user.getRoles());
        doc.setCreatedAt(user.getCreatedAt());
        doc.setResetToken(user.getResetToken());
        doc.setResetTokenExpiry(user.getResetTokenExpiry());
        return doc;
    }
}
