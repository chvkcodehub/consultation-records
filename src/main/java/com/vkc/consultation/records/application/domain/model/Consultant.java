package com.vkc.consultation.records.application.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "Consultant")

public class Consultant {
    @Id
    private String id;
    private String code;
    private String name;
    private String speciality;
    private double fee;
}
