package com.vkc.consultation.records.application.domain.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Data
@Document(collection = "Patient")

public class Patient {
    @Id
    private String id;
    private String code;
    private String name;
    private String gender;
    private Date dob;
    private String condition;
    private String address;
    private String phone;
    private String email;
    private Date startDate;
}
