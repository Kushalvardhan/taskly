package com.taskly.backend.task;

import com.taskly.backend.task.dto.projection.AssignmentSummaryProjection;
import com.taskly.backend.task.dto.projection.MyAssignmentProjection;
import com.taskly.backend.task.dto.projection.TaskListItemProjection;
import com.taskly.backend.task.enums.TaskPriority;
import com.taskly.backend.task.enums.TaskStatus;
import com.taskly.backend.task.enums.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndProject_Id(Long taskId, Long projectId);

    /*
     * QUERY 1:
     * Fetch filtered and paginated task IDs.
     *
     * EXISTS is used for assignment-related filters
     * to avoid duplicate task IDs.
     */

    @Query(
            value = """
                    SELECT t.id
                    FROM Task t
                    WHERE t.project.id = :projectId

                    AND (
                        :onlyMyTasks = false
                        OR EXISTS (
                            SELECT 1
                            FROM TaskAssignee ta
                            WHERE ta.task = t
                            AND ta.assignee.id = :userId
                        )
                    )

                    AND (
                        :onlyCreatedByMe = false
                        OR t.creator.id = :userId
                    )

                    AND (
                        :status IS NULL
                        OR t.status = :status
                    )

                    AND (
                        :myStatus IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM TaskAssignee ta
                            WHERE ta.task = t
                            AND ta.assignee.id = :userId
                            AND ta.status = :myStatus
                        )
                    )

                    AND (
                        :priority IS NULL
                        OR t.priority = :priority
                    )

                    AND (
                        :type IS NULL
                        OR t.type = :type
                    )

                    AND (
                        :assigneeId IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM TaskAssignee ta
                            WHERE ta.task = t
                            AND ta.assignee.id = :assigneeId
                        )
                    )

                    AND (
                        :search = ''
                        OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                    """,

            countQuery = """
                    SELECT COUNT(t.id)
                    FROM Task t
                    WHERE t.project.id = :projectId

                    AND (
                        :onlyMyTasks = false
                        OR EXISTS (
                            SELECT 1
                            FROM TaskAssignee ta
                            WHERE ta.task = t
                            AND ta.assignee.id = :userId
                        )
                    )

                    AND (
                        :onlyCreatedByMe = false
                        OR t.creator.id = :userId
                    )

                    AND (
                        :status IS NULL
                        OR t.status = :status
                    )

                    AND (
                        :myStatus IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM TaskAssignee ta
                            WHERE ta.task = t
                            AND ta.assignee.id = :userId
                            AND ta.status = :myStatus
                        )
                    )

                    AND (
                        :priority IS NULL
                        OR t.priority = :priority
                    )

                    AND (
                        :type IS NULL
                        OR t.type = :type
                    )

                    AND (
                        :assigneeId IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM TaskAssignee ta
                            WHERE ta.task = t
                            AND ta.assignee.id = :assigneeId
                        )
                    )

                    AND (
                        :search = ''
                        OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                    """
    )
    Page<Long> findFilteredTaskIds(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId,
            @Param("onlyMyTasks") boolean onlyMyTasks,
            @Param("onlyCreatedByMe") boolean onlyCreatedByMe,
            @Param("status") TaskStatus status,
            @Param("myStatus") TaskStatus myStatus,
            @Param("priority") TaskPriority priority,
            @Param("type") TaskType type,
            @Param("assigneeId") Long assigneeId,
            @Param("search") String search,
            Pageable pageable
    );


    /*
     * QUERY 2:
     * Fetch task details and creator information.
     *
     * Uses a DTO-style interface projection.
     */

    @Query("""
            SELECT
                t.id AS taskId,
                t.title AS title,
                t.description AS description,
                t.type AS type,
                t.priority AS priority,
                t.status AS overallStatus,
                t.deadline AS deadline,
                creator.id AS creatorId,
                creator.name AS creatorName

            FROM Task t

            JOIN t.creator creator

            WHERE t.id IN :taskIds
            """)
    List<TaskListItemProjection> findTaskDetailsByIds(
            @Param("taskIds") List<Long> taskIds
    );


    /*
     * QUERY 3:
     * Calculate assignment summary for every task.
     *
     * GROUP BY produces one result per task.
     */

    @Query("""
            SELECT
                ta.task.id AS taskId,

                COUNT(ta.id) AS totalAssignees,

                SUM(
                    CASE
                        WHEN ta.status = :completed THEN 1
                        ELSE 0
                    END
                ) AS completed,

                SUM(
                    CASE
                        WHEN ta.status = :inProgress THEN 1
                        ELSE 0
                    END
                ) AS inProgress,

                SUM(
                    CASE
                        WHEN ta.status = :todo THEN 1
                        ELSE 0
                    END
                ) AS todo,

                SUM(
                    CASE
                        WHEN ta.status = :cancelled THEN 1
                        ELSE 0
                    END
                ) AS cancelled

            FROM TaskAssignee ta

            WHERE ta.task.id IN :taskIds

            GROUP BY ta.task.id
            """)
    List<AssignmentSummaryProjection> findAssignmentSummaries(
            @Param("taskIds") List<Long> taskIds,
            @Param("completed") TaskStatus completed,
            @Param("inProgress") TaskStatus inProgress,
            @Param("todo") TaskStatus todo,
            @Param("cancelled") TaskStatus cancelled
    );


    /*
     * QUERY 4:
     * Fetch only the authenticated user's assignments.
     */

    @Query("""
            SELECT
                ta.task.id AS taskId,
                ta.status AS status

            FROM TaskAssignee ta

            WHERE ta.task.id IN :taskIds

            AND ta.assignee.id = :userId
            """)
    List<MyAssignmentProjection> findMyAssignments(
            @Param("taskIds") List<Long> taskIds,
            @Param("userId") Long userId
    );
}
