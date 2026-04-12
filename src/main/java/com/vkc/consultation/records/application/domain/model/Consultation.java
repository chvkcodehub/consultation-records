package com.vkc.consultation.records.application.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Data
@Document(collection = "Consultation")

public class Consultation {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
