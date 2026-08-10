package com.vkc.consultation.records.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vkc.consultation.records.application.domain.model.SpecialityType;

import lombok.Data;

@Data
@Document(collection = "Consultant")
public class ConsultantDocument {
    @Id
    private String id;
    private String name;
    private String email;
    private String mobile;
    private SpecialityType speciality;
    private String qualification;
    private int experienceYears;
    private double fee;
}
