package com.taskly.backend.task;

import com.taskly.backend.constants.BaseEntity;
import com.taskly.backend.user.TasklyUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "task_assignees",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_task_assignee",
            columnNames = {"task_id", "user_id"}
        )
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskAssignee extends BaseEntity {

    public TaskAssignee(Task task, TasklyUser user) {
        super();
        this.task = task;
        this.assignee = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private TasklyUser assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;
}