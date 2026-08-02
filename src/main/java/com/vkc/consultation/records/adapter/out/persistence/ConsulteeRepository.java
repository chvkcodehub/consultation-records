package com.vkc.consultation.records.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsulteeDocument;

public interface ConsulteeRepository extends MongoRepository<ConsulteeDocument, String> {
    @Query("{code : ?0}")
    Optional<ConsulteeDocument> findByCode(String code);

    @Query("{email : ?0}")
    Optional<ConsulteeDocument> findByEmail(String email);
}
