package com.taskly.backend.task;

import com.taskly.backend.task.dto.projection.AssigneePreviewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
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

                        FROM task_assignee ta

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

}
