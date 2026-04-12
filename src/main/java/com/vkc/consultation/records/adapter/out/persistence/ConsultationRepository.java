package com.vkc.consultation.records.adapter.out.persistence;

import com.vkc.consultation.records.application.domain.model.Consultation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ConsultationRepository extends MongoRepository<Consultation, String> {
    @Query("{code : ?0}")
    Consultation findConsultationByCode(String code);
    @Query("{consultantCode : ?0}")
    List <Consultation> findConsultationsByConsultant(String consultantCode);
    @Query("{patientCode : ?0}")
    List <Consultation> findConsultationsByPatient(String patientCode);

}
