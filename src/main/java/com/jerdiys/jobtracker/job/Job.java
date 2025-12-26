package com.jerdiys.jobtracker.job;

import com.jerdiys.jobtracker.enums.EmploymentType;
import com.jerdiys.jobtracker.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType; // e.g., Applied, Interviewing, Offered, Rejected

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    private LocalDateTime createdAt = LocalDateTime.now();
}
