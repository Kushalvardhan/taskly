package com.taskly.backend.task;

import com.taskly.backend.common.ApiResponse;
import com.taskly.backend.task.dto.*;
import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskScope;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/{projectId}/task/create")
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(
            Authentication authentication,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequestDto request
    ) {

        String email = authentication.getName();

        TaskResponseDto data =
                taskService.createTask(email, projectId, request);

        ApiResponse<TaskResponseDto> response = new ApiResponse<>(
                "Task created successfully",
                HttpStatus.CREATED.value(),
                Instant.now(),
                data
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{projectId}/tasks/{taskId}/assignees/options")
    public ResponseEntity<ApiResponse<List<TaskAssignmentStatusDto>>> getTaskAssignmentOptions(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        String email = authentication.getName();

        List<TaskAssignmentStatusDto> data =
                taskService.getTaskAssignmentOptions(
                        email, projectId, taskId
                );

        ApiResponse<List<TaskAssignmentStatusDto>> response =
                new ApiResponse<>(
                        "Task assignment options retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignees")
    public ResponseEntity<ApiResponse<List<TaskAssignmentStatusDto>>> taskAssignmentGroup(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody List<Long> userIds
    ) {
        String email = authentication.getName();

        List<TaskAssignmentStatusDto> data =
                taskService.taskAssignmentGroup(
                        email,
                        projectId,
                        taskId,
                        userIds
                );

        ApiResponse<List<TaskAssignmentStatusDto>> response =
                new ApiResponse<>(
                        "Task assignees assigned successfully",
                        HttpStatus.CREATED.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignee")
    public ResponseEntity<ApiResponse<TaskSelfAssignmentDto>> selfTaskAssignment(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        String email = authentication.getName();

        TaskSelfAssignmentDto data =
                taskService.selfTaskAssignment(
                        email,
                        projectId,
                        taskId
                );

        ApiResponse<TaskSelfAssignmentDto> response =
                new ApiResponse<>(
                        "Successfully assigned task!",
                        HttpStatus.CREATED.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<ApiResponse<TaskListResponseDto>> getProjectTasks(

            @PathVariable
            @Positive
            Long projectId,

            // Scope filters
            @RequestParam(defaultValue = "ALL")
            TaskScope scope,

            // Task filters
            @RequestParam(required = false)
            TaskStatus status,

            @RequestParam(required = false)
            TaskStatus myStatus,

            @RequestParam(required = false)
            TaskPriority priority,

            @RequestParam(required = false)
            TaskType type,

            @RequestParam(required = false)
            @Positive
            Long assigneeId,

            @RequestParam(required = false)
            String search,

            // Sorting
            @RequestParam(defaultValue = "deadline")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            String sortDirection,

            // Pagination
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            // Authenticated user
            Authentication authentication

    ) {

        String email = authentication.getName();

        TaskListResponseDto data = taskService.getProjectTasks(
                email,
                projectId,
                scope,
                status,
                myStatus,
                priority,
                type,
                assigneeId,
                search,
                sortBy,
                sortDirection,
                page,
                size
        );

        ApiResponse<TaskListResponseDto> response = new ApiResponse<>(
                "Project tasks retrieved successfully",
                HttpStatus.OK.value(),
                Instant.now(),
                data
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/task/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailsResponseDto>> getTaskDetails(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Authentication authentication
    ) {

        TaskDetailsResponseDto data =
                taskService.getTaskDetails(
                        authentication.getName(),
                        projectId,
                        taskId
                );

        ApiResponse<TaskDetailsResponseDto> response =
                new ApiResponse<>(
                        "Task details retrieved successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/task/{taskId}")
    public ResponseEntity<ApiResponse<UpdateTaskResponseDto>> updateTaskDetails(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequestDto taskDto,
            Authentication authentication
    ){
        UpdateTaskResponseDto data =
                taskService.updateTaskDetails(
                        authentication.getName(),
                        projectId,
                        taskId,
                        taskDto
                );

        ApiResponse<UpdateTaskResponseDto> response =
                new ApiResponse<>(
                        "Task details updated successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/task/{taskId}/assignees/me/status")
    public ResponseEntity<ApiResponse<UpdateAssignmentStatusDto>>
    updateMyAssignmentStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateAssignmentStatusRequestDto request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        UpdateAssignmentStatusDto data =
                taskService.updateMyAssignmentStatus(
                        email,
                        projectId,
                        taskId,
                        request
                );

        ApiResponse<UpdateAssignmentStatusDto> response =
                new ApiResponse<>(
                        "Assignment status updated successfully",
                        HttpStatus.OK.value(),
                        Instant.now(),
                        data
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            "/{projectId}/task/{taskId}/assignees/me"
    )
    public ResponseEntity<ApiResponse<Void>> removeMyAssignment(

            @PathVariable Long projectId,
            @PathVariable Long taskId,

            Authentication authentication
    ) {

        String email = authentication.getName();

        taskService.removeMyAssignment(
                email,
                projectId,
                taskId
        );

        ApiResponse<Void> response = new ApiResponse<>(
                "Your task assignment has been removed successfully",
                HttpStatus.OK.value(),
                Instant.now(),
                null
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            "/{projectId}/task/{taskId}/assignees/{userId}"
    )
    public ResponseEntity<ApiResponse<Void>> removeMemberAssignment(

            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long userId,

            Authentication authentication
    ) {

        String email = authentication.getName();

        taskService.removeMemberAssignment(
                email,
                projectId,
                taskId,
                userId
        );

        ApiResponse<Void> response = new ApiResponse<>(
                "Member assignment has been removed successfully",
                HttpStatus.OK.value(),
                Instant.now(),
                null
        );

        return ResponseEntity.ok(response);
    }
}