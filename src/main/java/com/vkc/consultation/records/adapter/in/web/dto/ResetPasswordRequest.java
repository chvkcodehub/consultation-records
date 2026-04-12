package com.vkc.consultation.records.adapter.in.web.dto;

public record ResetPasswordRequest(String email, String resetToken, String newPassword) {}
