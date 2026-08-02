package com.vkc.consultation.records.security;

public record AuthenticatedUser(String email, String role, String consulteeId) {}
