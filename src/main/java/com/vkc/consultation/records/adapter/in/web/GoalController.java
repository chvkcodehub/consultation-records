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

import com.vkc.consultation.records.adapter.in.web.dto.CreateGoalRequest;
import com.vkc.consultation.records.adapter.in.web.dto.GoalResponse;
import com.vkc.consultation.records.adapter.in.web.dto.UpdateGoalRequest;
import com.vkc.consultation.records.application.port.in.CreateGoalCommand;
import com.vkc.consultation.records.application.port.in.GoalUseCase;
import com.vkc.consultation.records.application.port.in.UpdateGoalCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/goals")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Tag(name = "Goals", description = "CRUD operations for health goals")
public class GoalController {

    private final GoalUseCase goalUseCase;

    public GoalController(GoalUseCase goalUseCase) {
        this.goalUseCase = goalUseCase;
    }

    @GetMapping
    @ResponseBody
    @Operation(summary = "List all goals", description = "Returns a list of all health goals.")
    @ApiResponse(responseCode = "200", description = "Goal list retrieved successfully")
    public List<GoalResponse> fetchGoals() {
        return goalUseCase.findGoals().stream()
                .map(GoalResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/id/{id}")
    @ResponseBody
    @Operation(summary = "Find goal by ID", description = "Returns a single goal by its MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal found",
                content = @Content(schema = @Schema(implementation = GoalResponse.class))),
        @ApiResponse(responseCode = "404", description = "Goal not found", content = @Content)
    })
    public GoalResponse findGoalById(
            @Parameter(description = "MongoDB document ID of the goal", required = true)
            @PathVariable String id) {
        return GoalResponse.from(goalUseCase.findGoalById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(summary = "Create a goal", description = "Creates a new health goal. Returns the created record with its assigned ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Goal created successfully",
                content = @Content(schema = @Schema(implementation = GoalResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public GoalResponse createGoal(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Goal details to create", required = true)
            @RequestBody CreateGoalRequest request) {
        CreateGoalCommand command = new CreateGoalCommand(
                request.name(), request.description(),
                request.importance(), request.difficulty(),
                request.achievingAgeYears(), request.achievingAgeMonths(),
                request.remarks(), request.periodInMonths(),
                request.createdDate(), request.status());
        return GoalResponse.from(goalUseCase.createGoal(command));
    }

    @PutMapping("/id/{id}")
    @ResponseBody
    @Operation(summary = "Update a goal", description = "Updates all fields of an existing goal identified by its MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal updated successfully",
                content = @Content(schema = @Schema(implementation = GoalResponse.class))),
        @ApiResponse(responseCode = "404", description = "Goal not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public GoalResponse updateGoal(
            @Parameter(description = "MongoDB document ID of the goal to update", required = true)
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated goal details", required = true)
            @RequestBody UpdateGoalRequest request) {
        UpdateGoalCommand command = new UpdateGoalCommand(
                request.name(), request.description(),
                request.importance(), request.difficulty(),
                request.achievingAgeYears(), request.achievingAgeMonths(),
                request.remarks(), request.periodInMonths(),
                request.updatedDate(), request.status());
        return GoalResponse.from(goalUseCase.updateGoal(id, command));
    }

    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a goal", description = "Deletes an existing goal by its MongoDB document ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found", content = @Content)
    })
    public void deleteGoal(
            @Parameter(description = "MongoDB document ID of the goal to delete", required = true)
            @PathVariable String id) {
        goalUseCase.deleteGoal(id);
    }
}
