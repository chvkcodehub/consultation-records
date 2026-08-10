package com.vkc.consultation.records.adapter.in.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vkc.consultation.records.adapter.in.web.dto.ConsultantResponse;
import com.vkc.consultation.records.adapter.in.web.dto.CreateConsultantRequest;
import com.vkc.consultation.records.adapter.in.web.dto.UpdateConsultantRequest;
import com.vkc.consultation.records.application.port.in.ConsultantUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsultantCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsultantCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/consultants")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Tag(name = "Consultants", description = "CRUD operations for medical consultants")
public class ConsultantController {

    private final ConsultantUseCase consultantUseCase;

    public ConsultantController(ConsultantUseCase consultantUseCase) {
        this.consultantUseCase = consultantUseCase;
    }

    @GetMapping
    @ResponseBody
    @Operation(summary = "List all consultants", description = "Returns a list of all registered consultants.")
    @ApiResponse(responseCode = "200", description = "Consultant list retrieved successfully")
    public List<ConsultantResponse> fetchConsultants() {
        return consultantUseCase.findConsultants().stream()
                .map(ConsultantResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/id/{id}")
    @ResponseBody
    @Operation(summary = "Find consultant by ID", description = "Returns a single consultant by their MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consultant found",
                content = @Content(schema = @Schema(implementation = ConsultantResponse.class))),
        @ApiResponse(responseCode = "404", description = "Consultant not found", content = @Content)
    })
    public ConsultantResponse findConsultantById(
            @Parameter(description = "MongoDB document ID of the consultant", required = true)
            @PathVariable String id) {
        return ConsultantResponse.from(consultantUseCase.findConsultantById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(summary = "Create a consultant", description = "Registers a new consultant. Returns the created record with its assigned ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Consultant created successfully",
                content = @Content(schema = @Schema(implementation = ConsultantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ConsultantResponse createConsultant(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Consultant details to create", required = true)
            @RequestBody CreateConsultantRequest request) {
        CreateConsultantCommand command = new CreateConsultantCommand(
            request.name(), request.email(), request.mobile(), request.speciality(),
                request.qualification(), request.experienceYears(), request.fee());
        return ConsultantResponse.from(consultantUseCase.createConsultant(command));
    }

    @PutMapping("/id/{id}")
    @ResponseBody
    @Operation(summary = "Update a consultant", description = "Updates all fields of an existing consultant identified by their MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consultant updated successfully",
                content = @Content(schema = @Schema(implementation = ConsultantResponse.class))),
        @ApiResponse(responseCode = "404", description = "Consultant not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ConsultantResponse updateConsultant(
            @Parameter(description = "MongoDB document ID of the consultant to update", required = true)
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated consultant details", required = true)
            @RequestBody UpdateConsultantRequest request) {
        UpdateConsultantCommand command = new UpdateConsultantCommand(
                request.name(), request.email(), request.mobile(), request.speciality(),
                request.qualification(), request.experienceYears(), request.fee());
        return ConsultantResponse.from(consultantUseCase.updateConsultant(id, command));
    }

    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a consultant", description = "Deletes an existing consultant by their MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Consultant deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Consultant not found", content = @Content)
    })
    public void deleteConsultant(
            @Parameter(description = "MongoDB document ID of the consultant to delete", required = true)
            @PathVariable String id) {
        consultantUseCase.deleteConsultant(id);
    }
}
