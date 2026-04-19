package com.vkc.consultation.records.application.domain.model;

import lombok.Data;

@Data
public class Consultant {
    private String id;
    private String code;
    private String name;
    private String speciality;
    private String qualification;
    private int experienceYears;
    private double fee;
}
