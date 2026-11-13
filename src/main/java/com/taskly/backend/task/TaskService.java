package com.taskly.backend.task;

import com.taskly.backend.access.ProjectAccessService;
import com.taskly.backend.access.TaskAccessService;
import com.taskly.backend.access.UserAccessService;
import com.taskly.backend.project.*;
import com.taskly.backend.task.dto.*;
import com.taskly.backend.task.dto.projection.AssigneePreviewProjection;
import com.taskly.backend.task.dto.projection.AssignmentSummaryProjection;
import com.taskly.backend.task.dto.projection.MyAssignmentProjection;
import com.taskly.backend.task.dto.projection.TaskListItemProjection;
import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskScope;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;
import com.taskly.backend.user.TasklyUser;
import com.taskly.backend.user.TasklyUserRepository;
import lombok.RequiredArgsConstructor;
import com.taskly.backend.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
                assignee.getId(),
                assignee.getTask().getId(),
                assignee.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public TaskListResponseDto getProjectTasks(

            String email, Long projectId, TaskScope scope, TaskStatus status,

            TaskStatus myStatus, TaskPriority priority, TaskType type, Long assigneeId,

            String search, String sortBy, String sortDirection, int page, int size

    ) {

        // --------------------------------------------------
        // STEP 1: Find authenticated user
        // --------------------------------------------------

        TasklyUser currentUser =
                userAccessService.getUserByEmail(email);


        // --------------------------------------------------
        // STEP 2: Verify project access
        // --------------------------------------------------

        projectAccessService.getProjectForUser(
                projectId,
                currentUser
        );

        Long userId = currentUser.getId();


        // --------------------------------------------------
        // STEP 3: Validate sorting
        // --------------------------------------------------

        Sort sort = getTaskSort(sortBy, sortDirection);

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );


        // --------------------------------------------------
        // STEP 4: Convert scope into boolean flags
        // --------------------------------------------------

        boolean onlyMyTasks =
                scope == TaskScope.MY_TASKS;

        boolean onlyCreatedByMe =
                scope == TaskScope.CREATED_BY_ME;


        // --------------------------------------------------
        // STEP 5: Normalize search
        // --------------------------------------------------

        if (search == null || search.isBlank()) {
            search = "";
        } else {
            search = search.trim();
        }


        // --------------------------------------------------
        // STEP 6: QUERY 1 - Fetch paginated task IDs
        // --------------------------------------------------

        Page<Long> taskIdPage =
                taskRepository.findFilteredTaskIds(

                        projectId,

                        userId,

                        onlyMyTasks,

                        onlyCreatedByMe,

                        status,

                        myStatus,

                        priority,

                        type,

                        assigneeId,

                        search,

                        pageable
                );


        List<Long> taskIds = taskIdPage.getContent();


        // --------------------------------------------------
        // STEP 7: Handle empty page
        // --------------------------------------------------

        if (taskIds.isEmpty()) {

            return new TaskListResponseDto(

                    List.of(),

                    taskIdPage.getNumber(),

                    taskIdPage.getSize(),

                    taskIdPage.getTotalElements(),

                    taskIdPage.getTotalPages(),

                    taskIdPage.isLast()

            );
        }


        // --------------------------------------------------
        // STEP 8: QUERY 2 - Fetch task details
        // --------------------------------------------------

        List<TaskListItemProjection> taskDetails =
                taskRepository.findTaskDetailsByIds(taskIds);


        Map<Long, TaskListItemProjection> taskDetailsMap =
                taskDetails.stream()
                        .collect(Collectors.toMap(
                                TaskListItemProjection::getTaskId,
                                Function.identity()
                        ));


        // --------------------------------------------------
        // STEP 9: QUERY 3 - Fetch assignment summaries
        // --------------------------------------------------

        List<AssignmentSummaryProjection> summaries =
                taskRepository.findAssignmentSummaries(

                        taskIds,

                        TaskStatus.COMPLETED,

                        TaskStatus.IN_PROGRESS,

                        TaskStatus.TODO,

                        TaskStatus.CANCELLED
                );


        Map<Long, AssignmentSummaryProjection> summaryMap =
                summaries.stream()
                        .collect(Collectors.toMap(
                                AssignmentSummaryProjection::getTaskId,
                                Function.identity()
                        ));


        // --------------------------------------------------
        // STEP 10: QUERY 4 - Fetch current user's assignments
        // --------------------------------------------------

        List<MyAssignmentProjection> myAssignments =
                taskRepository.findMyAssignments(
                        taskIds,
                        userId
                );


        Map<Long, MyAssignmentProjection> myAssignmentMap =
                myAssignments.stream()
                        .collect(Collectors.toMap(
                                MyAssignmentProjection::getTaskId,
                                Function.identity()
                        ));


        // --------------------------------------------------
        // STEP 11: QUERY 5 - Fetch assignee previews
        // --------------------------------------------------

        List<AssigneePreviewProjection> previews =
                taskAssigneeRepository.findAssigneePreviews(taskIds);

        Map<Long, List<AssigneePreviewDto>> previewMap =
                new HashMap<>();

        for (AssigneePreviewProjection preview : previews) {

            AssigneePreviewDto previewDto =
                    new AssigneePreviewDto(
                            preview.getUserId(), preview.getName()
                    );

            previewMap.computeIfAbsent(
                    preview.getTaskId(),
                    key -> new ArrayList<>()
            ).add(previewDto);
        }


        // --------------------------------------------------
        // STEP 12: Assemble final response
        // --------------------------------------------------

        List<TaskListItemResponseDto> responseContent =
                new ArrayList<>();


        // IMPORTANT:
        // Iterate over taskIds, not taskDetails.
        // This preserves the database pagination order.

        for (Long taskId : taskIds) {


            // ----------------------------------------------
            // Get task details
            // ----------------------------------------------

            TaskListItemProjection task =
                    taskDetailsMap.get(taskId);


            // ----------------------------------------------
            // Get assignment summary
            // ----------------------------------------------

            AssignmentSummaryProjection summary =
                    summaryMap.get(taskId);


            AssignmentSummaryDto assignmentSummary;

            if (summary == null) {

                // No assignees

                assignmentSummary = new AssignmentSummaryDto(

                        0L,

                        0L,

                        0L,

                        0L,

                        0L
                );

            } else {

                assignmentSummary = new AssignmentSummaryDto(

                        summary.getTotalAssignees(),

                        summary.getCompleted(),

                        summary.getInProgress(),

                        summary.getTodo(),

                        summary.getCancelled()
                );
            }


            // ----------------------------------------------
            // Get current user's assignment
            // ----------------------------------------------

            MyAssignmentProjection myAssignment =
                    myAssignmentMap.get(taskId);


            MyAssignmentDto myAssignmentDto;

            if (myAssignment == null) {

                myAssignmentDto = new MyAssignmentDto(

                        false,

                        null
                );

            } else {

                myAssignmentDto = new MyAssignmentDto(

                        true,

                        myAssignment.getStatus()
                );
            }


            // ----------------------------------------------
            // Get assignee previews
            // ----------------------------------------------

            List<AssigneePreviewDto> assigneePreview =
                    previewMap.getOrDefault(
                            taskId,
                            List.of()
                    );


            // ----------------------------------------------
            // Get creator information
            // ----------------------------------------------

            CreatedByDto createdBy =
                    new CreatedByDto(

                            task.getCreatorId(),

                            task.getCreatorName()
                    );


            // ----------------------------------------------
            // Construct task response
            // ----------------------------------------------

            TaskListItemResponseDto taskResponse =
                    new TaskListItemResponseDto(

                            task.getTaskId(),

                            task.getTitle(),

                            task.getDescription(),

                            task.getType(),

                            task.getPriority(),

                            task.getOverallStatus(),

                            task.getDeadline(),

                            createdBy,

                            assignmentSummary,

                            myAssignmentDto,

                            assigneePreview
                    );

            responseContent.add(taskResponse);
        }


        // --------------------------------------------------
        // STEP 13: Return paginated response
        // --------------------------------------------------

        return new TaskListResponseDto(

                responseContent,

                taskIdPage.getNumber(),

                taskIdPage.getSize(),

                taskIdPage.getTotalElements(),

                taskIdPage.getTotalPages(),

                taskIdPage.isLast()
        );
    }


    // ======================================================
    // SORT VALIDATION
    // ======================================================

    private Sort getTaskSort(
            String sortBy,
            String sortDirection
    ) {

        // Only allow known entity properties.

        Map<String, String> allowedSortFields = Map.of(

                "deadline", "deadline",

                "createdAt", "createdAt",

                "priority", "priority",

                "title", "title",

                "status", "status"
        );


        String sortProperty =
                allowedSortFields.get(sortBy);


        if (sortProperty == null) {

            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }


        Sort.Direction direction;

        try {

            direction = Sort.Direction.valueOf(
                    sortDirection.toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid sort direction. Use ASC or DESC."
            );
        }


        // Add task ID as a secondary sort field.
        // This makes pagination more deterministic
        // when multiple tasks have the same sort value.

        return Sort.by(direction, sortProperty)
                .and(Sort.by(Sort.Direction.DESC, "id"));
    }

}
