package com.vkc.consultation.records.adapter.out.persistence;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsultationDocument;

public interface ConsultationRepository extends MongoRepository<ConsultationDocument, String> {
    @Query("{consultantId : ?0}")
    List<ConsultationDocument> findConsultationsByConsultant(String consultantId);
    @Query("{consulteeId : ?0}")
    List<ConsultationDocument> findConsultationsByConsultee(String consulteeId);
}
