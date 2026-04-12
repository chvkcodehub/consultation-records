package com.vkc.consultation.records.adapter.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.application.domain.model.Consultation;
import com.vkc.consultation.records.application.port.in.ConsultationUseCase;
@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ConsultationController {
    private final ConsultationUseCase consultationUseCase;

    public ConsultationController(ConsultationUseCase consultationUseCase) {
        this.consultationUseCase = consultationUseCase;
    }

    // ...
    @GetMapping(path = "/consultations")
    @ResponseBody
    public List<Consultation> fetchConsultations() {
        return consultationUseCase.findConsultations();
    }
    @GetMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public Consultation findConsultation(@PathVariable String id) {
        return consultationUseCase.findConsultationById(id);
    }
    @GetMapping(path = "/consultations/code/{code}")
    @ResponseBody
    public Consultation findConsultationByCode(@PathVariable String code) {
        return consultationUseCase.findConsultationByCode(code);
    }

    @GetMapping(path = "/consultations/consultant/{consultantCode}")
    @ResponseBody
    public List<Consultation> findConsultationByConsultant(@PathVariable String consultantCode) {
        return consultationUseCase.findConsultationByConsultant(consultantCode);
    }
    @GetMapping(path = "/consultations/patient/{patientCode}")
    @ResponseBody
    public List<Consultation> findConsultationByPatient(@PathVariable String patientCode) {
        return consultationUseCase.findConsultationByPatient(patientCode);
    }

    @PostMapping(path = "/consultations")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Consultation createConsultation(@RequestBody Consultation consultation) {
        return consultationUseCase.createConsultation(consultation);
    }

    @PutMapping(path = "/consultations/id/{id}")
    @ResponseBody
    public Consultation updateConsultation(@PathVariable String id, @RequestBody Consultation consultation) {
        return consultationUseCase.updateConsultation(id, consultation);
    }

    @DeleteMapping(path = "/consultations/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConsultation(@PathVariable String id) {
        consultationUseCase.deleteConsultation(id);
    }

}
