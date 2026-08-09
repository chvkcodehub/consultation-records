package com.vkc.consultation.records.adapter.out.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.domain.model.ConsultationType;

import lombok.Data;

@Data
@Document(collection = "Consultation")
public class ConsultationDocument {
    @Id
    private String id;
    private ConsultationType type;
    private ConsultationStatus status;
    private String consultantId;
    private String consulteeId;
    private String diagnosis;
    private String prescription;
    private String comments;
    private Date consultationDate;
    private Date followUpDate;
    private Date updatedDate;
    private String createdBy;
    private BigDecimal fee;
}
