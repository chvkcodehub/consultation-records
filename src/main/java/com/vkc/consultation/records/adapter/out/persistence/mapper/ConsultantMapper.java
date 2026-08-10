package com.vkc.consultation.records.adapter.out.persistence.mapper;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsultantDocument;
import com.vkc.consultation.records.application.domain.model.Consultant;

public class ConsultantMapper {

    private ConsultantMapper() {}

    public static Consultant toDomain(ConsultantDocument doc) {
        Consultant consultant = new Consultant();
        consultant.setId(doc.getId());
        consultant.setName(doc.getName());
        consultant.setEmail(doc.getEmail());
        consultant.setMobile(doc.getMobile());
        consultant.setSpeciality(doc.getSpeciality());
        consultant.setQualification(doc.getQualification());
        consultant.setExperienceYears(doc.getExperienceYears());
        consultant.setFee(doc.getFee());
        return consultant;
    }

    public static ConsultantDocument toDocument(Consultant consultant) {
        ConsultantDocument doc = new ConsultantDocument();
        doc.setId(consultant.getId());
        doc.setName(consultant.getName());
        doc.setEmail(consultant.getEmail());
        doc.setMobile(consultant.getMobile());
        doc.setSpeciality(consultant.getSpeciality());
        doc.setQualification(consultant.getQualification());
        doc.setExperienceYears(consultant.getExperienceYears());
        doc.setFee(consultant.getFee());
        return doc;
    }
}
