package com.taskly.backend.project;

import com.taskly.backend.project.dto.ProjectMemberListDto;
import com.taskly.backend.user.TasklyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectAndUser(
            OrgProject project,
            TasklyUser user
    );

    @Query("""
        SELECT new com.taskly.backend.project.dto.ProjectMemberListDto(
            pm.id,
            pm.project.id,
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
}
