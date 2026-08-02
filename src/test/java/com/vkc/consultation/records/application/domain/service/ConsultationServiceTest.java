package com.vkc.consultation.records.application.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.vkc.consultation.records.application.domain.model.Consultant;
import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.domain.model.ConsultationStatus;
import com.vkc.consultation.records.application.domain.model.ConsultationType;
import com.vkc.consultation.records.application.port.in.BookConsultationCommand;
import com.vkc.consultation.records.application.port.out.ConsultantPort;
import com.vkc.consultation.records.application.port.out.ConsultationPort;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationPort consultationPort;

    @Mock
    private ConsultantPort consultantPort;

    @Test
    void bookConsultation_setsBookedStatusAndCopiesConsultantFee() {
        Consultant consultant = new Consultant();
        consultant.setCode("DR001");
        consultant.setFee(150.0);
        when(consultantPort.findByCode("DR001")).thenReturn(Optional.of(consultant));
        when(consultationPort.saveConsultation(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ConsultationService service = new ConsultationService(consultationPort, consultantPort);
        BookConsultationCommand command = new BookConsultationCommand(
                "PT001", "DR001", ConsultationType.INITIAL_CONSULTATION, new Date(), "First visit");

        Consultation result = service.bookConsultation(command);

        assertThat(result.getStatus()).isEqualTo(ConsultationStatus.BOOKED);
        assertThat(result.getPatientCode()).isEqualTo("PT001");
        assertThat(result.getConsultantCode()).isEqualTo("DR001");
        assertThat(result.getFee()).isEqualByComparingTo("150.0");
        assertThat(result.getCode()).isNotBlank();
    }

    @Test
    void bookConsultation_throwsNotFoundWhenConsultantMissing() {
        when(consultantPort.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        ConsultationService service = new ConsultationService(consultationPort, consultantPort);
        BookConsultationCommand command = new BookConsultationCommand(
                "PT001", "UNKNOWN", ConsultationType.INITIAL_CONSULTATION, new Date(), null);

        assertThatThrownBy(() -> service.bookConsultation(command))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Consultant not found");
    }

    @Test
    void findConsultationForPatient_returnsRecordWhenOwnedByPatient() {
        Consultation consultation = new Consultation();
        consultation.setId("abc123");
        consultation.setPatientCode("PT001");
        when(consultationPort.findConsultationById("abc123")).thenReturn(consultation);

        ConsultationService service = new ConsultationService(consultationPort, consultantPort);

        assertThat(service.findConsultationForPatient("abc123", "PT001")).isSameAs(consultation);
    }

    @Test
    void findConsultationForPatient_throwsForbiddenWhenNotOwner() {
        Consultation consultation = new Consultation();
        consultation.setId("abc123");
        consultation.setPatientCode("PT001");
        when(consultationPort.findConsultationById("abc123")).thenReturn(consultation);

        ConsultationService service = new ConsultationService(consultationPort, consultantPort);

        assertThatThrownBy(() -> service.findConsultationForPatient("abc123", "PT999"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not authorized");
    }
}
