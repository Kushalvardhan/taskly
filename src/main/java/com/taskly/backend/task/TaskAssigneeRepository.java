package com.taskly.backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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
}
