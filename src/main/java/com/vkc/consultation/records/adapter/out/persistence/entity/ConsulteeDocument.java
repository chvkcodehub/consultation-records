package com.vkc.consultation.records.adapter.out.persistence.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Consultee")
public class ConsulteeDocument {
    @Id
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
