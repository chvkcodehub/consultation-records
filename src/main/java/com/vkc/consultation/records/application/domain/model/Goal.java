package com.vkc.consultation.records.application.domain.model;

import java.util.Date;

import lombok.Data;

@Data
public class Goal {
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
