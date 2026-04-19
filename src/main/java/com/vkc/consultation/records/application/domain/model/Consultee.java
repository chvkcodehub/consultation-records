package com.vkc.consultation.records.application.domain.model;

import java.util.Date;

import lombok.Data;

@Data
public class Consultee {
    private String id;
    private String code;
    private String name;
    private String gender;
    private Date dob;
    private String address;
    private String email;
    private String phone;
    private Date startDate;
    private String condition;
    private String recoveryStatus;
    
}
