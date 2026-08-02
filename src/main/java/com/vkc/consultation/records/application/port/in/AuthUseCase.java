package com.vkc.consultation.records.application.port.in;

public interface AuthUseCase {
    AuthResult register(String email, String password);
    AuthResult login(String email, String password);
    AuthResult registerConsultee(RegisterConsulteeCommand command);
    void forgotPassword(String email);
    void resetPassword(String email, String resetToken, String newPassword);
}
