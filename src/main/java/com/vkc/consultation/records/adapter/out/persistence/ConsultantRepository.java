package com.vkc.consultation.records.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vkc.consultation.records.adapter.out.persistence.entity.ConsultantDocument;

public interface ConsultantRepository extends MongoRepository<ConsultantDocument, String> {
}
