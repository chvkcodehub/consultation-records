package com.vkc.consultation.records.application.port.out;

public interface TokenPort {
    String generateToken(String subject, String role, String consulteeCode);
}
