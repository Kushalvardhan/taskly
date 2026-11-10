package com.taskly.backend.task;

import com.taskly.backend.access.ProjectAccessService;
import com.taskly.backend.access.TaskAccessService;
import com.taskly.backend.access.UserAccessService;
import com.taskly.backend.project.*;
import com.taskly.backend.task.dto.*;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import lombok.RequiredArgsConstructor;
import com.taskly.backend.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TasklyUserRepository tasklyUserRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;

    private final UserAccessService userAccessService;
    private final ProjectAccessService projectAccessService;
    private final TaskAccessService taskAccessService;

    @Transactional
    public CreateTaskResponseDto createTask(String email, Long projectId, CreateTaskRequestDto request) {
        // Find authenticated user
        TasklyUser currentUser =
                userAccessService.getUserByEmail(email);

        // Verify project access
        OrgProject project =
                projectAccessService.getProjectForUser(
                        projectId,
                        currentUser
                );

        // Verify project membership
        ProjectMember membership =
                projectAccessService.getProjectMembership(
                        projectId,
                        currentUser.getId()
                );

        ProjectRole role = membership.getProjectRole();

        // Create task
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

        // Automatically assign the creator if they are a MEMBER
        if (role == ProjectRole.MEMBER) {

            TaskAssignee taskAssignee = new TaskAssignee();

            taskAssignee.setTask(savedTask);
            taskAssignee.setAssignee(currentUser);
            taskAssignee.setStatus(TaskStatus.TODO);

            savedAssignee =
                    taskAssigneeRepository.save(taskAssignee);

            message = "Task created with successful self assignment";
        }

        Long assigneeId = savedAssignee != null
                ? savedAssignee.getAssignee().getId()
                : null;

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

        return new CreateTaskResponseDto(message, response);
    }

    @Transactional(readOnly = true)
    public GetTaskAssignmentStatusResponse getTaskAssignmentOptions(
            String email, Long projectId, Long taskId
    ) {
        // Find authenticated user
        TasklyUser currentUser =
                userAccessService.getUserByEmail(email);

        // Verify project access
        projectAccessService.getProjectForUser(
                projectId,
                currentUser
        );

        // Verify Project Manager role
        projectAccessService.verifyProjectManager(
                projectId,
                currentUser.getId()
        );

        // Verify task belongs to the requested project
        taskAccessService.getTaskByIdAndProjectId(
                taskId,
                projectId
        );

        // Retrieve project members with their assignment status
        List<TaskAssignmentStatusDto> team =
                projectMemberRepository.findTaskAssignmentOptions(
                        projectId,
                        taskId
                );

        return new GetTaskAssignmentStatusResponse(
                "Task assignment options retrieved successfully",
                team
        );
    }

    @Transactional
    public GetTaskAssignmentStatusResponse taskAssignmentGroup(
            String email, Long projectId, Long taskId, List<Long> assigneeIds
    ) {

        // Find authenticated user
        TasklyUser currentUser =
                userAccessService.getUserByEmail(email);

        // Verify project access
        projectAccessService.getProjectForUser(
                projectId,
                currentUser
        );

        // Verify Project Manager role
        projectAccessService.verifyProjectManager(
                projectId,
                currentUser.getId()
        );

        // Verify task belongs to the requested project
        Task task =
                taskAccessService.getTaskByIdAndProjectId(
                        taskId,
                        projectId
                );

        // Validate request
        if (assigneeIds == null || assigneeIds.isEmpty()) {
            throw new BadRequestException(
                    "At least one assignee must be selected"
            );
        }

        // Reject duplicate IDs
        Set<Long> requestedIds = new HashSet<>(assigneeIds);

        if (requestedIds.size() != assigneeIds.size()) {
            throw new BadRequestException(
                    "Duplicate assignee IDs are not allowed"
            );
        }

        // Verify all requested users exist
        List<TasklyUser> users =
                tasklyUserRepository.findAllById(requestedIds);

        Set<Long> existingUserIds = users.stream()
                .map(TasklyUser::getId)
                .collect(Collectors.toSet());

        if (!existingUserIds.equals(requestedIds)) {

            Set<Long> invalidUserIds = new HashSet<>(requestedIds);
            invalidUserIds.removeAll(existingUserIds);

            throw new BadRequestException(
                    "Invalid user IDs: " + invalidUserIds
            );
        }

        // Verify all requested users belong to this project
        Set<Long> projectMemberIds =
                projectMemberRepository.findMemberUserIds(
                        projectId,
                        requestedIds
                );

        if (!projectMemberIds.equals(requestedIds)) {

            Set<Long> nonMemberIds = new HashSet<>(requestedIds);
            nonMemberIds.removeAll(projectMemberIds);

            throw new BadRequestException(
                    "The following users are not members of this project: "
                            + nonMemberIds
            );
        }

        // Check for existing assignments
        Set<Long> alreadyAssignedIds =
                taskAssigneeRepository.findAlreadyAssignedUserIds(
                        taskId,
                        requestedIds
                );

        if (!alreadyAssignedIds.isEmpty()) {
            throw new BadRequestException(
                    "The following users are already assigned: "
                            + alreadyAssignedIds
            );
        }

        // Create assignments
        List<TaskAssignee> taskAssignees = users.stream()
                .map(user -> new TaskAssignee(task, user))
                .toList();

        taskAssigneeRepository.saveAll(taskAssignees);

        // Fetch updated assignment options
        List<TaskAssignmentStatusDto> team =
                projectMemberRepository.findTaskAssignmentOptions(
                        projectId,
                        taskId
                );

        return new GetTaskAssignmentStatusResponse(
                "Task assignees assigned successfully",
                team
        );
    }

    @Transactional
    public TaskSelfAssignmentDto selfTaskAssignment(
            String email, Long projectId, Long taskId
    ) {
        // Find authenticated user
        TasklyUser currentUser =
                userAccessService.getUserByEmail(email);

        // Verify project access
        projectAccessService.getProjectForUser(
                projectId,
                currentUser
        );

        //verify current user's membership in the intended project
        projectAccessService.getProjectMembership(
                projectId,
                currentUser.getId()
        );

        // Verify task belongs to the requested project
        Task task =
                taskAccessService.getTaskByIdAndProjectId(
                        taskId,
                        projectId
                );

        // Check for existing assignments
        boolean alreadyAssigned =
                taskAssigneeRepository.existsByTask_IdAndAssignee_Id(
                        taskId,
                        currentUser.getId()
                );

        if (alreadyAssigned) {
            throw new BadRequestException(
                    "You are already assigned to the task!"
            );
        }

        //Create Assignment
        TaskAssignee taskAssignee = new TaskAssignee(task, currentUser);

        TaskAssignee assignee = taskAssigneeRepository.save(taskAssignee);

        return new TaskSelfAssignmentDto(
                "Successfully assigned task!",
                assignee
        );
    }
}
