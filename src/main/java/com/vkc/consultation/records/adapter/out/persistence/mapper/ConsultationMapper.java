package com.vkc.consultation.records.adapter.out.persistence.mapper;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsultationDocument;
import com.vkc.consultation.records.application.domain.model.Consultation;

public class ConsultationMapper {

    private ConsultationMapper() {}

    public static Consultation toDomain(ConsultationDocument doc) {
        Consultation domain = new Consultation();
        domain.setId(doc.getId());
        domain.setType(doc.getType());
        domain.setStatus(doc.getStatus());
        domain.setConsultantId(doc.getConsultantId());
        domain.setPatientId(doc.getPatientId());
        domain.setDiagnosis(doc.getDiagnosis());
        domain.setPrescription(doc.getPrescription());
        domain.setComments(doc.getComments());
        domain.setConsultationDate(doc.getConsultationDate());
        domain.setFollowUpDate(doc.getFollowUpDate());
        domain.setUpdatedDate(doc.getUpdatedDate());
        domain.setCreatedBy(doc.getCreatedBy());
        domain.setFee(doc.getFee());
        return domain;
    }

    public static ConsultationDocument toDocument(Consultation domain) {
        ConsultationDocument doc = new ConsultationDocument();
        doc.setId(domain.getId());
        doc.setType(domain.getType());
        doc.setStatus(domain.getStatus());
        doc.setConsultantId(domain.getConsultantId());
        doc.setPatientId(domain.getPatientId());
        doc.setDiagnosis(domain.getDiagnosis());
        doc.setPrescription(domain.getPrescription());
        doc.setComments(domain.getComments());
        doc.setConsultationDate(domain.getConsultationDate());
        doc.setFollowUpDate(domain.getFollowUpDate());
        doc.setUpdatedDate(domain.getUpdatedDate());
        doc.setCreatedBy(domain.getCreatedBy());
        doc.setFee(domain.getFee());
        return doc;
    }
}
