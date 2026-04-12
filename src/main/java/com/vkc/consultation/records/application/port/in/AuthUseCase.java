package com.vkc.consultation.records.application.port.in;

public interface AuthUseCase {
    String register(String email, String password);
    String login(String email, String password);
    void forgotPassword(String email);
    void resetPassword(String email, String resetToken, String newPassword);
}
