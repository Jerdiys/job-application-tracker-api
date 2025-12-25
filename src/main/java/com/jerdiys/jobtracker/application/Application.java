package com.jerdiys.jobtracker.application;

import com.jerdiys.jobtracker.enums.ApplicationStatus;
import com.jerdiys.jobtracker.job.Job;
import com.jerdiys.jobtracker.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "applications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_id", "candidate_id"})})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job jobId;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidateId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private LocalDateTime appliedAt = LocalDateTime.now();
}
