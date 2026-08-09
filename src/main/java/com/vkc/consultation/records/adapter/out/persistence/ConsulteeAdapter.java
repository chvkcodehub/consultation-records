package com.vkc.consultation.records.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.vkc.consultation.records.adapter.out.persistence.mapper.ConsulteeMapper;
import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.port.out.ConsulteePort;

@Component
public class ConsulteeAdapter implements ConsulteePort {

    private final ConsulteeRepository consulteeRepository;

    public ConsulteeAdapter(ConsulteeRepository consulteeRepository) {
        this.consulteeRepository = consulteeRepository;
    }

    @Override
    public List<Consultee> findAll() {
        return consulteeRepository.findAll().stream()
                .map(ConsulteeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Consultee> findById(@NonNull String id) {
        return consulteeRepository.findById(id).map(ConsulteeMapper::toDomain);
    }

    @Override
    public Optional<Consultee> findByEmail(String email) {
        return consulteeRepository.findByEmail(email).map(ConsulteeMapper::toDomain);
    }

    @Override
    public Consultee save(@NonNull Consultee consultee) {
        return ConsulteeMapper.toDomain(
                consulteeRepository.save(ConsulteeMapper.toDocument(consultee)));
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return consulteeRepository.existsById(id);
    }

    @Override
    public void deleteById(@NonNull String id) {
        consulteeRepository.deleteById(id);
    }
}
