package com.taskly.backend.project;

import com.taskly.backend.project.dto.ProjectMemberListDto;
import com.taskly.backend.task.dto.TaskAssignmentStatusDto;
import com.taskly.backend.user.TasklyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectAndUser(
            OrgProject project,
            TasklyUser user
    );

    @Query("""
        SELECT new com.taskly.backend.project.dto.ProjectMemberListDto(
            pm.user.id,
            pm.user.name,
            pm.user.email,
            pm.projectRole
        )
        FROM ProjectMember pm
        WHERE pm.project.id = :projectId
        ORDER BY pm.joinedAt ASC
    """)
    List<ProjectMemberListDto> findProjectMembers(
            @Param("projectId") Long projectId
    );

    Optional<ProjectMember>findByProject_IdAndUser_Id(Long projectId, Long userId);

    @Query("""
    SELECT new com.taskly.backend.task.dto.TaskAssignmentOptionDto(
        pm.user.id,
        pm.user.name,
        pm.user.email,
        pm.projectRole,
        CASE WHEN ta.id IS NOT NULL THEN true ELSE false END
    )
    FROM ProjectMember pm
    LEFT JOIN TaskAssignee ta
        ON ta.assignee.id = pm.user.id
        AND ta.task.id = :taskId
    WHERE pm.project.id = :projectId
    ORDER BY pm.user.name
""")
    List<TaskAssignmentStatusDto> findTaskAssignmentOptions(
            @Param("projectId") Long projectId,
            @Param("taskId") Long taskId
    );

    @Query("""
            SELECT pm.user.id
            FROM ProjectMember pm
            WHERE pm.project.id = :projectId
            AND pm.user.id IN :userIds
            """)
    Set<Long> findMemberUserIds(
            @Param("projectId") Long projectId,
            @Param("userIds") Collection<Long> userIds
    );

    Optional<ProjectMember> findByProjectIdAndUserId(
            Long projectId,
            Long userId
    );
}
