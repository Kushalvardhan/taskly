package com.taskly.backend.task;

import com.taskly.backend.task.dto.CreateTaskRequestDto;
import com.taskly.backend.task.dto.CreateTaskResponseDto;
import com.taskly.backend.task.dto.GetTaskAssignmentOptionsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getTaskAssignmentOptions(
            Authentication authentication,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ){
        String email = authentication.getName();

        GetTaskAssignmentOptionsResponse response =
                taskService.getTaskAssignmentOptions(email, projectId, taskId);

        return ResponseEntity.ok(response);
    }
}
