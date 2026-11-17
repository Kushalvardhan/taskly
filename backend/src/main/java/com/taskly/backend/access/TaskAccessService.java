package com.taskly.backend.access;

import com.taskly.backend.exception.ResourceNotFoundException;
import com.taskly.backend.task.Task;
import com.taskly.backend.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskAccessService {

    private final TaskRepository taskRepository;

    // Fetch task within a specific project
    public Task getTaskByIdAndProjectId(
            Long taskId,
            Long projectId
    ) {

        return taskRepository.findByIdAndProject_Id(taskId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found in this project"
                        )
                );
    }
}