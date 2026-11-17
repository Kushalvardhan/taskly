package com.taskly.backend.project;

import com.taskly.backend.constants.BaseEntity;
import com.taskly.backend.organisation.Organisation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "org_projects")
@Getter
@Setter
public class OrgProject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false, updatable = false)
    private Organisation organisation;

    @Column(nullable = false)
    private String name;

    private String description;
}
