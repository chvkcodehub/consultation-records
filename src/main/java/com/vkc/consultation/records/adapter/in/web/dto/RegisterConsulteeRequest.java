package com.vkc.consultation.records.adapter.in.web.dto;

import java.util.Date;

public record RegisterConsulteeRequest(
        String email,
        String password,
        String name,
        String gender,
        Date dob,
        String address,
        String phone) {}
