package com.vkc.consultation.records.application.port.in;

public record AuthResult(String token, String role, String consulteeId, String consultantId,
	boolean passwordChangeRequired) {}
