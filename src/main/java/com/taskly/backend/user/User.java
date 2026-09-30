package com.taskly.backend.user;

import com.taskly.backend.organisation.Organisation;
import com.taskly.backend.organisation.OrganisationRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = true)
    private Organisation organisation;

    @Enumerated(EnumType.STRING)
    private OrganisationRole organisationRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
