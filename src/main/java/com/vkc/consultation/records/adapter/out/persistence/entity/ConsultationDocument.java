package com.vkc.consultation.records.adapter.out.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Consultation")
public class ConsultationDocument {
    @Id
    private String id;
    private String code;
    private String type;
    private String consultantCode;
    private String patientCode;
    private String diagnosis;
    private String prescription;
    private String comments;
    private Date consultationDate;
    private Date followUpDate;
    private Date updatedDate;
    private String createdBy;
    private BigDecimal fee;
}
