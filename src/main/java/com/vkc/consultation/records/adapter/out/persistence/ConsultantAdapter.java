package com.vkc.consultation.records.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.vkc.consultation.records.adapter.out.persistence.mapper.ConsultantMapper;
import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.port.out.ConsultantPort;

@Component
public class ConsultantAdapter implements ConsultantPort {

    private final ConsultantRepository consultantRepository;

    public ConsultantAdapter(ConsultantRepository consultantRepository) {
        this.consultantRepository = consultantRepository;
    }

    @Override
    public List<Consultant> findAll() {
        return consultantRepository.findAll().stream()
                .map(ConsultantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Consultant> findById(@NonNull String id) {
        return consultantRepository.findById(id).map(ConsultantMapper::toDomain);
    }

    @Override
    public Consultant save(@NonNull Consultant consultant) {
        return ConsultantMapper.toDomain(
                consultantRepository.save(ConsultantMapper.toDocument(consultant)));
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return consultantRepository.existsById(id);
    }

    @Override
    public void deleteById(@NonNull String id) {
        consultantRepository.deleteById(id);
    }
}
