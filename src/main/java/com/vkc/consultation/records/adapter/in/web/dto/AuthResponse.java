package com.vkc.consultation.records.adapter.in.web.dto;

import com.vkc.consultation.records.application.port.in.AuthResult;

public record AuthResponse(String token, String role, String consulteeId, String consultantId,
        boolean passwordChangeRequired) {

    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(result.token(), result.role(), result.consulteeId(), result.consultantId(),
                result.passwordChangeRequired());
    }
}
