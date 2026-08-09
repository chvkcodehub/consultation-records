package com.vkc.consultation.records.application.port.in;

import com.vkc.consultation.records.application.domain.model.ConsultantSummaryReport;
import com.vkc.consultation.records.application.domain.model.ConsulteeSessionsReport;

public interface ReportUseCase {
    ConsulteeSessionsReport getConsulteeSessionsReport();
    ConsultantSummaryReport getConsultantSummaryReport();
}
