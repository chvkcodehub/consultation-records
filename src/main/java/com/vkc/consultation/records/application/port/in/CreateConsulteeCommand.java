package com.vkc.consultation.records.application.port.in;

import java.util.Date;

public record CreateConsulteeCommand(
        String name,
        String gender,
        Date dob,
        String condition,
        String address,
        String phone,
        String email,
        Date startDate) {}
