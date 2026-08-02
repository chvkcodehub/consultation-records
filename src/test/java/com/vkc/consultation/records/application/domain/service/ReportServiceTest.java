package com.vkc.consultation.records.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.ConsultantSummaryBreakdown;
import com.vkc.consultation.records.application.domain.model.ConsultantSummaryReport;
import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.domain.model.ConsultationType;
import com.vkc.consultation.records.application.domain.model.Consultee;
import com.vkc.consultation.records.application.domain.model.ConsulteeSessionsReport;
import com.vkc.consultation.records.application.port.out.ConsultantPort;
import com.vkc.consultation.records.application.port.out.ConsultationPort;
import com.vkc.consultation.records.application.port.out.ConsulteePort;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ConsultationPort consultationPort;

    @Mock
    private ConsulteePort consulteePort;

    @Mock
    private ConsultantPort consultantPort;

    private Consultation consultation(String patientId, String consultantId, ConsultationType type) {
        Consultation c = new Consultation();
        c.setPatientId(patientId);
        c.setConsultantId(consultantId);
        c.setType(type);
        return c;
    }

    private Consultee consultee(String id, String name) {
        Consultee c = new Consultee();
        c.setId(id);
        c.setName(name);
        return c;
    }

    private Consultant consultant(String id, String name) {
        Consultant c = new Consultant();
        c.setId(id);
        c.setName(name);
        return c;
    }

    @Test
    void getConsulteeSessionsReport_countsSessionsPerConsultee() {
        when(consultationPort.findConsultations()).thenReturn(List.of(
                consultation("PT001", "DR001", ConsultationType.INITIAL_CONSULTATION),
                consultation("PT001", "DR002", ConsultationType.FOLLOW_UP),
                consultation("PT002", "DR001", ConsultationType.FOLLOW_UP)));
        when(consulteePort.findAll()).thenReturn(List.of(
                consultee("PT001", "Alice"), consultee("PT002", "Bob")));

        ReportService service = new ReportService(consultationPort, consulteePort, consultantPort);
        ConsulteeSessionsReport report = service.getConsulteeSessionsReport();

        assertThat(report.getTotalSessions()).isEqualTo(3);
        assertThat(report.getBreakdown()).hasSize(2);
        assertThat(report.getBreakdown()).anySatisfy(b -> {
            assertThat(b.getConsulteeId()).isEqualTo("PT001");
            assertThat(b.getConsulteeName()).isEqualTo("Alice");
            assertThat(b.getSessionCount()).isEqualTo(2);
        });
        assertThat(report.getBreakdown()).anySatisfy(b -> {
            assertThat(b.getConsulteeId()).isEqualTo("PT002");
            assertThat(b.getSessionCount()).isEqualTo(1);
        });
    }

    @Test
    void getConsultantSummaryReport_countsSessionsAndTypesPerConsultant() {
        when(consultationPort.findConsultations()).thenReturn(List.of(
                consultation("PT001", "DR001", ConsultationType.INITIAL_CONSULTATION),
                consultation("PT002", "DR001", ConsultationType.INITIAL_CONSULTATION),
                consultation("PT003", "DR001", ConsultationType.FOLLOW_UP),
                consultation("PT001", "DR002", ConsultationType.EMERGENCY)));
        when(consultantPort.findAll()).thenReturn(List.of(
                consultant("DR001", "Dr. Smith"), consultant("DR002", "Dr. Jones")));

        ReportService service = new ReportService(consultationPort, consulteePort, consultantPort);
        ConsultantSummaryReport report = service.getConsultantSummaryReport();

        assertThat(report.getTotalConsultants()).isEqualTo(2);
        assertThat(report.getTotalSessions()).isEqualTo(4);

        ConsultantSummaryBreakdown dr001 = report.getBreakdown().stream()
                .filter(b -> b.getConsultantId().equals("DR001"))
                .findFirst().orElseThrow();
        assertThat(dr001.getConsultantName()).isEqualTo("Dr. Smith");
        assertThat(dr001.getSessionCount()).isEqualTo(3);
        assertThat(dr001.getByType()).hasSize(2);
        assertThat(dr001.getByType()).anySatisfy(tc -> {
            assertThat(tc.getType()).isEqualTo(ConsultationType.INITIAL_CONSULTATION);
            assertThat(tc.getCount()).isEqualTo(2);
        });
    }
}
