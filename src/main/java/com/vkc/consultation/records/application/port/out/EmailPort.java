package com.vkc.consultation.records.application.port.out;

public interface EmailPort {
    void sendConsultantWelcomeEmail(String consultantName, String consultantEmail, String temporaryPassword);
}