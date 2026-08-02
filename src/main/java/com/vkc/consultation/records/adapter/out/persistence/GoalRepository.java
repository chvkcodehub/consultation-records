package com.vkc.consultation.records.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.vkc.consultation.records.adapter.out.persistence.entity.GoalDocument;

public interface GoalRepository extends MongoRepository<GoalDocument, String> {
    @Query("{code : ?0}")
    Optional<GoalDocument> findByCode(String code);
}
