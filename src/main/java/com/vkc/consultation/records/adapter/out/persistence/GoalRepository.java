package com.vkc.consultation.records.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vkc.consultation.records.adapter.out.persistence.entity.GoalDocument;

public interface GoalRepository extends MongoRepository<GoalDocument, String> {
}
