package com.taskly.backend.task;

import com.taskly.backend.task.dto.CreateTaskRequestDto;
import com.taskly.backend.task.dto.CreateTaskResponseDto;
import com.taskly.backend.task.dto.GetTaskAssignmentStatusResponse;
import com.taskly.backend.task.dto.TaskSelfAssignmentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
