package com.taskly.backend.task;

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

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/{projectId}/task/create")
    public ResponseEntity<?> createTask(
            Authentication authentication,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequestDto request
            ){

        String email = authentication.getName();

        CreateTaskResponseDto response =
                taskService.createTask(email, projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{projectId}/tasks/{taskId}/assignees/options")
    public ResponseEntity<GetTaskAssignmentStatusResponse> getTaskAssignmentOptions(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ){
        String email = authentication.getName();

        GetTaskAssignmentStatusResponse response =
                taskService.getTaskAssignmentOptions(email, projectId, taskId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignees")
    public ResponseEntity<GetTaskAssignmentStatusResponse> taskAssignmentGroup(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody List<Long> userIds
    ) {
        String email = authentication.getName();

        GetTaskAssignmentStatusResponse response =
                taskService.taskAssignmentGroup(email, projectId, taskId, userIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignee")
    public ResponseEntity<TaskSelfAssignmentDto> selfTaskAssignment(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        String email = authentication.getName();

        TaskSelfAssignmentDto response =
                taskService.selfTaskAssignment(email, projectId, taskId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{projectId}/tasks")
    public TaskListResponseDto getProjectTasks(

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

        return taskService.getProjectTasks(

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
    }
}
