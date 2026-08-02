package com.vkc.consultation.records.adapter.in.web;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.ConsultantSummaryReportResponse;
import com.vkc.consultation.records.adapter.in.web.dto.ConsulteeSessionsReportResponse;
import com.vkc.consultation.records.application.port.in.ReportUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reports")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ReportController {

    private final ReportUseCase reportUseCase;

    public ReportController(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @GetMapping(path = "/reports/consultees/sessions")
    @ResponseBody
    public ConsulteeSessionsReportResponse consulteeSessionsReport() {
        return ConsulteeSessionsReportResponse.from(reportUseCase.getConsulteeSessionsReport());
    }

    @GetMapping(path = "/reports/consultants/summary")
    @ResponseBody
    public ConsultantSummaryReportResponse consultantSummaryReport() {
        return ConsultantSummaryReportResponse.from(reportUseCase.getConsultantSummaryReport());
    }
}
