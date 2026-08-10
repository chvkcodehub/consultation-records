package com.vkc.consultation.records.application.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class Consultation {
    private String id;
    private ConsultationType type;
    private ConsultationStatus status;
    private String consultantId;
    private String consulteeId;
    private String diagnosis;
    private String prescription;
    private String comments;
    private Integer rating;
    private String feedback;
    private Date consultationDate;
    private Date followUpDate;
    private Date updatedDate;
    private String createdBy;
    private BigDecimal fee;
}
