package com.vkc.consultation.records.application.domain.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.ConsultantSummaryBreakdown;
import com.vkc.consultation.records.application.domain.model.ConsultantSummaryReport;
import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.domain.model.ConsultationTypeCount;
import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.domain.model.ConsulteeSessionBreakdown;
import com.vkc.consultation.records.application.domain.model.ConsulteeSessionsReport;
import com.vkc.consultation.records.application.port.in.ReportUseCase;
import com.vkc.consultation.records.application.port.out.ConsultantPort;
import com.vkc.consultation.records.application.port.out.ConsultationPort;
import com.vkc.consultation.records.application.port.out.ConsulteePort;

@Service
public class ReportService implements ReportUseCase {

    private final ConsultationPort consultationPort;
    private final ConsulteePort consulteePort;
    private final ConsultantPort consultantPort;

    public ReportService(ConsultationPort consultationPort, ConsulteePort consulteePort,
            ConsultantPort consultantPort) {
        this.consultationPort = consultationPort;
        this.consulteePort = consulteePort;
        this.consultantPort = consultantPort;
    }

    @Override
    public ConsulteeSessionsReport getConsulteeSessionsReport() {
        List<Consultation> consultations = consultationPort.findConsultations();
        Map<String, String> consulteeNamesById = consulteePort.findAll().stream()
                .collect(Collectors.toMap(Consultee::getId, Consultee::getName, (a, b) -> a));

        Map<String, Long> sessionsByPatient = consultations.stream()
                .filter(c -> c.getPatientId() != null)
                .collect(Collectors.groupingBy(Consultation::getPatientId, Collectors.counting()));

        List<ConsulteeSessionBreakdown> breakdown = sessionsByPatient.entrySet().stream()
                .map(entry -> {
                    ConsulteeSessionBreakdown item = new ConsulteeSessionBreakdown();
                    item.setConsulteeId(entry.getKey());
                    item.setConsulteeName(consulteeNamesById.get(entry.getKey()));
                    item.setSessionCount(entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        ConsulteeSessionsReport report = new ConsulteeSessionsReport();
        report.setTotalSessions(consultations.size());
        report.setBreakdown(breakdown);
        return report;
    }

    @Override
    public ConsultantSummaryReport getConsultantSummaryReport() {
        List<Consultation> consultations = consultationPort.findConsultations();
        List<Consultant> consultants = consultantPort.findAll();
        Map<String, String> consultantNamesById = consultants.stream()
                .collect(Collectors.toMap(Consultant::getId, Consultant::getName, (a, b) -> a));

        Map<String, List<Consultation>> consultationsByConsultant = consultations.stream()
                .filter(c -> c.getConsultantId() != null)
                .collect(Collectors.groupingBy(Consultation::getConsultantId));

        List<ConsultantSummaryBreakdown> breakdown = consultationsByConsultant.entrySet().stream()
                .map(entry -> {
                    ConsultantSummaryBreakdown item = new ConsultantSummaryBreakdown();
                    item.setConsultantId(entry.getKey());
                    item.setConsultantName(consultantNamesById.get(entry.getKey()));
                    item.setSessionCount(entry.getValue().size());
                    item.setByType(entry.getValue().stream()
                            .collect(Collectors.groupingBy(Consultation::getType, Collectors.counting()))
                            .entrySet().stream()
                            .map(typeEntry -> {
                                ConsultationTypeCount typeCount = new ConsultationTypeCount();
                                typeCount.setType(typeEntry.getKey());
                                typeCount.setCount(typeEntry.getValue());
                                return typeCount;
                            })
                            .collect(Collectors.toList()));
                    return item;
                })
                .collect(Collectors.toList());

        ConsultantSummaryReport report = new ConsultantSummaryReport();
        report.setTotalConsultants(consultants.size());
        report.setTotalSessions(consultations.size());
        report.setBreakdown(breakdown);
        return report;
    }
}
