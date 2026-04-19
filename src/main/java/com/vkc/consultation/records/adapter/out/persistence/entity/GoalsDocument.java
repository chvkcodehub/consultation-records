package com.vkc.consultation.records.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Goals")
public class GoalsDocument {
    @Id
    private String id;
    private String code;
    private String name;
    private String description;
    private String importance;
    private String difficulty;
    private int achievingAgeYears;
    private int achievingAgeMonths;
    private String remarks;
    private int periodInMonths;
    private String createdDate;
    private String updatedDate;
    private int status;
}
