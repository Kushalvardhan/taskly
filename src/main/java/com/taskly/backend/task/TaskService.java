package com.taskly.backend.task;

import com.taskly.backend.organisation.OrganisationRole;
import com.taskly.backend.project.*;
import com.taskly.backend.task.dto.*;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TasklyUserRepository tasklyUserRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;

    @Transactional
    public CreateTaskResponseDto createTask(String email, Long projectId, CreateTaskRequestDto request) {
        // 1. Find User
        TasklyUser currentUser = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found")
                );

        // 2. Fetch the project
        OrgProject project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Project not found")
                );

        // 3. Verify that the user belongs to an organisation
        if (currentUser.getOrganisation() == null) {
            throw new AccessDeniedException(
                    "You must belong to an organisation to create tasks"
            );
        }

        // 4. Verify that the project belongs to the user's organisation
        if (!project.getOrganisation().getId()
                .equals(currentUser.getOrganisation().getId())) {

            throw new AccessDeniedException(
                    "You cannot create task in a project outside your organisation"
            );
        }

        // 5. Verify User is part of the project and get his project role
        ProjectMember projectMember = projectMemberRepository
                .findByProject_IdAndUser_Id(projectId, currentUser.getId())
                .orElseThrow(() -> new AccessDeniedException(
                        "You are not a member of this project"
                ));

        ProjectRole role = projectMember.getProjectRole();

        // 6. Create new task
        Task task = new Task();
        task.setProject(project);
        task.setCreator(currentUser);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStartDate(request.startDate());
        task.setDeadline(request.deadline());
        task.setType(request.type());
        task.setPriority(request.priority());
        task.setStatus(TaskStatus.TODO);

        Task savedTask = taskRepository.save(task);
        TaskAssignee savedAssignee = null;
        String message = "Task created successfully";

        // 6. check for user's self assignment
        if(role == ProjectRole.MEMBER){
            TaskAssignee taskAssignee = new TaskAssignee();
            taskAssignee.setTask(savedTask);
            taskAssignee.setAssignee(currentUser);
            taskAssignee.setStatus(TaskStatus.TODO);

            savedAssignee = taskAssigneeRepository.save(taskAssignee);
            message = "Task created with successful self assignment";
        };

        Long assigneeId = savedAssignee != null ? savedAssignee.getAssignee().getId() : null;

        // Create dto
        TaskResponseDto response = new TaskResponseDto(
                savedTask.getId(),
                savedTask.getProject().getId(),
                savedTask.getCreator().getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                assigneeId,
                savedTask.getStartDate(),
                savedTask.getDeadline(),
                savedTask.getType(),
                savedTask.getPriority(),
                savedTask.getStatus(),
                savedTask.getCreatedAt()
        );

        return new CreateTaskResponseDto(
                message,
                response
        );
    }

    @Transactional(readOnly = true)
    public GetTaskAssignmentOptionsResponse getTaskAssignmentOptions(
            String email, Long projectId, Long taskId
    ) {
        // 1. Fetch the authenticated user
        TasklyUser currentUser = tasklyUserRepository.findUserByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        // 2. Fetch the project
        OrgProject project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Project not found")
                );

        // 3. Verify that the user belongs to the project's organisation
        if (currentUser.getOrganisation() == null ||
                !project.getOrganisation().getId()
                        .equals(currentUser.getOrganisation().getId())) {

            throw new AccessDeniedException(
                    "You do not have access to this project"
            );
        }

        // 4. Verify that the current user is a member of this project
        ProjectMember currentMembership =
                projectMemberRepository.findByProject_IdAndUser_Id(
                                projectId,
                                currentUser.getId()
                        )
                        .orElseThrow(() ->
                                new AccessDeniedException(
                                        "You are not a member of this project"
                                )
                        );

        // 5. Only Project Managers can view assignment options
        if (currentMembership.getProjectRole() != ProjectRole.PROJECT_MANAGER) {
            throw new AccessDeniedException(
                    "Only Project Managers can view task assignment options"
            );
        }

        // 6. Verify that the task belongs to the requested project
        Task task = taskRepository.findByIdAndProject_Id(taskId, projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Task not found in this project"
                        )
                );

        // 7. Retrieve project members with their assignment status
        List<TaskAssignmentOptionDto> team =
                projectMemberRepository.findTaskAssignmentOptions(
                        projectId,
                        taskId
                );

        // 8. Return the response
        return new GetTaskAssignmentOptionsResponse(
                "Task assignment options retrieved successfully",
                team
        );
    }
}
