package com.vkc.consultation.records.adapter.out.persistence;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.vkc.consultation.records.adapter.out.persistence.mapper.ConsultationMapper;
import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.port.out.ConsultationPort;

@Component
public class ConsultationAdapter implements ConsultationPort {

    private final ConsultationRepository consultationRepository;

    public ConsultationAdapter(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    @Override
    public List<Consultation> findConsultations() {
        return consultationRepository.findAll().stream()
                .map(ConsultationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Consultation findConsultationById(@NonNull String id) {
        return consultationRepository.findById(id)
                .map(ConsultationMapper::toDomain)
                .orElseThrow();
    }

    @Override
    public List<Consultation> findConsultationsByConsultant(String consultantId) {
        return consultationRepository.findConsultationsByConsultant(consultantId).stream()
                .map(ConsultationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findConsultationsByPatient(String patientId) {
        return consultationRepository.findConsultationsByPatient(patientId).stream()
                .map(ConsultationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Consultation saveConsultation(@NonNull Consultation consultation) {
        return ConsultationMapper.toDomain(
                consultationRepository.save(ConsultationMapper.toDocument(consultation)));
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return consultationRepository.existsById(id);
    }

    @Override
    public void deleteById(@NonNull String id) {
        consultationRepository.deleteById(id);
    }
}
