package com.vkc.consultation.records.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsultantDocument;

public interface ConsultantRepository extends MongoRepository<ConsultantDocument, String> {
    @Query("{code : ?0}")
    Optional<ConsultantDocument> findByCode(String code);
}
