package com.taskly.backend.project;

import com.taskly.backend.project.dto.ProjectListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<OrgProject, Long> {

    @Query("""
        SELECT new com.taskly.backend.project.dto.ProjectListDto(
            p.id,
            p.name,

            (SELECT COUNT(pm.id)
             FROM ProjectMember pm
             WHERE pm.project = p),

            CASE WHEN myMembership IS NOT NULL
                 THEN true
                 ELSE false
            END,

            myMembership.projectRole
        )

        FROM OrgProject p

        LEFT JOIN ProjectMember myMembership
            ON myMembership.project = p
            AND myMembership.user.id = :userId

        WHERE p.organisation.id = :organisationId

        ORDER BY p.id
        """)
    List<ProjectListDto> findProjectSummaries(
            @Param("organisationId") Long organisationId,
            @Param("userId") Long userId
    );
}
