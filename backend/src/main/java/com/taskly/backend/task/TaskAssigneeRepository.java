package com.taskly.backend.task;

import com.taskly.backend.task.dto.projection.TaskAssigneeDetailsProjection;
import com.taskly.backend.task.dto.projection.AssigneePreviewProjection;
import com.taskly.backend.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, Long> {

    @Query("""
        SELECT ta.assignee.id
        FROM TaskAssignee ta
        WHERE ta.task.id = :taskId
        AND ta.assignee.id IN :userIds
        """)
    Set<Long> findAlreadyAssignedUserIds(
            @Param("taskId") Long taskId,
            @Param("userIds") Collection<Long> userIds
    );

    boolean existsByTask_IdAndAssignee_Id(Long taskId, Long assigneeId);

    /*
     * QUERY 5:
     * Fetch a maximum of three assignees per task.
     *
     * ROW_NUMBER() restarts numbering for each task.
     */

    @Query(
            value = """
                    SELECT
                        ranked.task_id AS "taskId",
                        ranked.user_id AS "userId",
                        ranked.name AS "name"

                    FROM (
                        SELECT
                            ta.task_id,
                            u.id AS user_id,
                            u.name,

                            ROW_NUMBER() OVER (
                                PARTITION BY ta.task_id
                                ORDER BY ta.created_at ASC, ta.id ASC
                            ) AS row_num

                        FROM task_assignees ta

                        JOIN users u
                            ON u.id = ta.user_id

                        WHERE ta.task_id IN (:taskIds)

                    ) ranked

                    WHERE ranked.row_num <= 3
                    """,
            nativeQuery = true
    )
    List<AssigneePreviewProjection> findAssigneePreviews(
            @Param("taskIds") List<Long> taskIds
    );

    /*
     * QUERY 7:
     * Fetch all assignees of a particular task,
     * including their project roles and assignment statuses.
     */

    @Query("""
        SELECT
            assignee.id AS userId,
            assignee.name AS name,
            projectMember.projectRole AS projectRole,
            ta.status AS status

        FROM TaskAssignee ta

        JOIN ta.assignee assignee

        JOIN ProjectMember projectMember
            ON projectMember.user = assignee
            AND projectMember.project = ta.task.project

        WHERE ta.task.id = :taskId
        AND ta.task.project.id = :projectId

        ORDER BY ta.createdAt ASC, ta.id ASC
        """)
    List<TaskAssigneeDetailsProjection> findTaskAssigneeDetails(
            @Param("taskId") Long taskId,
            @Param("projectId") Long projectId
    );

    Optional<TaskAssignee> findByTask_IdAndAssignee_Id(
            Long taskId,
            Long assigneeId
    );

    boolean existsByTask_IdAndStatus(
            Long taskId,
            TaskStatus status
    );

    boolean existsByTask_IdAndStatusNot(
            Long taskId,
            TaskStatus status
    );

    boolean existsByTask_Id(Long taskId);

}
