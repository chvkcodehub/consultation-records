package com.vkc.consultation.records.application.domain.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "Goals")

public class Goals {
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
