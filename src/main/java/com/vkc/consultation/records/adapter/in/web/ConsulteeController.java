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

import com.vkc.consultation.records.adapter.in.web.dto.ConsulteeResponse;
import com.vkc.consultation.records.adapter.in.web.dto.CreateConsulteeRequest;
import com.vkc.consultation.records.adapter.in.web.dto.UpdateConsulteeRequest;
import com.vkc.consultation.records.application.port.in.ConsulteeUseCase;
import com.vkc.consultation.records.application.port.in.CreateConsulteeCommand;
import com.vkc.consultation.records.application.port.in.UpdateConsulteeCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/consultees")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Tag(name = "Consultees", description = "CRUD operations for consultees (patients)")
public class ConsulteeController {

    private final ConsulteeUseCase consulteeUseCase;

    public ConsulteeController(ConsulteeUseCase consulteeUseCase) {
        this.consulteeUseCase = consulteeUseCase;
    }

    @GetMapping
    @ResponseBody
    @Operation(summary = "List all consultees", description = "Returns a list of all registered consultees.")
    @ApiResponse(responseCode = "200", description = "Consultee list retrieved successfully")
    public List<ConsulteeResponse> fetchConsultees() {
        return consulteeUseCase.findConsultees().stream()
                .map(ConsulteeResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/id/{id}")
    @ResponseBody
    @Operation(summary = "Find consultee by ID", description = "Returns a single consultee by their MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consultee found",
                content = @Content(schema = @Schema(implementation = ConsulteeResponse.class))),
        @ApiResponse(responseCode = "404", description = "Consultee not found", content = @Content)
    })
    public ConsulteeResponse findConsulteeById(
            @Parameter(description = "MongoDB document ID of the consultee", required = true)
            @PathVariable String id) {
        return ConsulteeResponse.from(consulteeUseCase.findConsulteeById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(summary = "Create a consultee", description = "Registers a new consultee. Returns the created record with its assigned ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Consultee created successfully",
                content = @Content(schema = @Schema(implementation = ConsulteeResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ConsulteeResponse createConsultee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Consultee details to create", required = true)
            @RequestBody CreateConsulteeRequest request) {
        CreateConsulteeCommand command = new CreateConsulteeCommand(
                request.name(), request.gender(),
                request.dob(), request.condition(), request.address(),
                request.phone(), request.email(), request.startDate());
        return ConsulteeResponse.from(consulteeUseCase.createConsultee(command));
    }

    @PutMapping("/id/{id}")
    @ResponseBody
    @Operation(summary = "Update a consultee", description = "Updates all fields of an existing consultee identified by their MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consultee updated successfully",
                content = @Content(schema = @Schema(implementation = ConsulteeResponse.class))),
        @ApiResponse(responseCode = "404", description = "Consultee not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ConsulteeResponse updateConsultee(
            @Parameter(description = "MongoDB document ID of the consultee to update", required = true)
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated consultee details", required = true)
            @RequestBody UpdateConsulteeRequest request) {
        UpdateConsulteeCommand command = new UpdateConsulteeCommand(
                request.name(), request.gender(),
                request.dob(), request.condition(), request.address(),
                request.phone(), request.email(), request.startDate());
        return ConsulteeResponse.from(consulteeUseCase.updateConsultee(id, command));
    }

    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a consultee", description = "Deletes an existing consultee by their MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Consultee deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Consultee not found", content = @Content)
    })
    public void deleteConsultee(
            @Parameter(description = "MongoDB document ID of the consultee to delete", required = true)
            @PathVariable String id) {
        consulteeUseCase.deleteConsultee(id);
    }
}
