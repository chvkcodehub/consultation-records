package com.vkc.consultation.records.adapter.out.persistence.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Goal")
public class GoalDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private String importance;
    private String difficulty;
    private int achievingAgeYears;
    private int achievingAgeMonths;
    private String remarks;
    private int periodInMonths;
    private Date createdDate;
    private Date updatedDate;
    private String status;
}
