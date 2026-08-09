package com.vkc.consultation.records.adapter.out.persistence.mapper;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsulteeDocument;
import com.vkc.consultation.records.application.domain.model.Consultee;

public class ConsulteeMapper {

    private ConsulteeMapper() {}

    public static Consultee toDomain(ConsulteeDocument doc) {
        Consultee consultee = new Consultee();
        consultee.setId(doc.getId());
        consultee.setName(doc.getName());
        consultee.setGender(doc.getGender());
        consultee.setDob(doc.getDob());
        consultee.setAddress(doc.getAddress());
        consultee.setEmail(doc.getEmail());
        consultee.setPhone(doc.getPhone());
        consultee.setStartDate(doc.getStartDate());
        consultee.setCondition(doc.getCondition());
        consultee.setRecoveryStatus(doc.getRecoveryStatus());
        return consultee;
    }

    public static ConsulteeDocument toDocument(Consultee consultee) {
        ConsulteeDocument doc = new ConsulteeDocument();
        doc.setId(consultee.getId());
        doc.setName(consultee.getName());
        doc.setGender(consultee.getGender());
        doc.setDob(consultee.getDob());
        doc.setAddress(consultee.getAddress());
        doc.setPhone(consultee.getPhone());
        doc.setEmail(consultee.getEmail());
        doc.setStartDate(consultee.getStartDate());
        doc.setCondition(consultee.getCondition());
         doc.setRecoveryStatus(consultee.getRecoveryStatus());
        return doc;
    }
}
