package com.vkc.consultation.records.application.port.in;

import java.util.Date;

public record RegisterConsulteeCommand(
        String email,
        String password,
        String name,
        String gender,
        Date dob,
        String address,
        String phone) {}
