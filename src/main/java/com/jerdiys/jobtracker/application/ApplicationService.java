package com.jerdiys.jobtracker.application;

import com.jerdiys.jobtracker.dtos.ApplicationResponse;
import com.jerdiys.jobtracker.dtos.JobResponse;
import com.jerdiys.jobtracker.enums.ApplicationStatus;
import com.jerdiys.jobtracker.exception.ForbiddenException;
import com.jerdiys.jobtracker.exception.ResourceNotFoundException;
import com.jerdiys.jobtracker.job.Job;
import com.jerdiys.jobtracker.job.JobRepo;
import com.jerdiys.jobtracker.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final UserRepo userRepo;
    private final JobRepo jobRepo;

    @Autowired
    public ApplicationService(ApplicationRepo applicationRepo, UserRepo userRepo, JobRepo jobRepo) {
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
        this.applicationRepo = applicationRepo;
    }

    public ApplicationResponse applyToJob(Authentication auth, Long jobId) {
        String userEmail = auth.getName();
        var user = userRepo.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var job = jobRepo.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepo.existsByJobAndCandidate(job, user)) {
            throw new IllegalArgumentException("You have already applied to this job");
        }

        Application application = new Application();
        application.setCandidate(user);
        application.setJob(job);

        Application savedApplication = applicationRepo.save(application);
        JobResponse jobResponse = mapToJobResponse(job);

        return ApplicationResponse.builder()
                .applicationId(savedApplication.getId())
                .candidate(savedApplication.getCandidate().getName())
                .job(jobResponse)
                .status(savedApplication.getStatus())
                .appliedAt(savedApplication.getAppliedAt().toString())
                .build();
    }

    public List<ApplicationResponse> getMyApplications(Authentication auth) {
        String userEmail = auth.getName();
        var user = userRepo.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Application> applications = applicationRepo.findByCandidate(user);

        return applications.stream().map(application -> ApplicationResponse.builder()
                .applicationId(application.getId())
                .job(mapToJobResponse(application.getJob()))
                .candidate(application.getCandidate().getName())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt().toString())
                .build()).toList();

    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId, Authentication auth) {
        String userEmail = auth.getName();
        var user = userRepo.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var job = jobRepo.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getId().equals(user.getId()) && !user.getRole().name().equals("ROLE_ADMIN")) {
            throw new ForbiddenException("You are not authorized to view applications for this job");
        }

        List<Application> applications = applicationRepo.findByJob(job);
        return applications.stream().map(application -> ApplicationResponse.builder()
                .applicationId(application.getId())
                .job(mapToJobResponse(application.getJob()))
                .candidate(application.getCandidate().getName())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt().toString())
                .build()).toList();
    }

    public List<ApplicationResponse> getAllApplications() {
        List<Application> applications = applicationRepo.findAll();
        return applications.stream().map(application -> ApplicationResponse.builder()
                .applicationId(application.getId())
                .job(mapToJobResponse(application.getJob()))
                .candidate(application.getCandidate().getName())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt().toString())
                .build()).toList();
    }

    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus newStatus,
            Authentication auth) {
        String userEmail = auth.getName();
        var user = userRepo.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        var job = application.getJob();

        if (!job.getRecruiter().getId().equals(user.getId()) && !user.getRole().name().equals("ROLE_ADMIN")) {
            throw new ForbiddenException("You are not authorized to update the application status for this job");
        }

        ApplicationStatus currentStatus = application.getStatus();

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        application.setStatus(newStatus);
        Application updatedApplication = applicationRepo.save(application);
        return ApplicationResponse.builder()
                .applicationId(updatedApplication.getId())
                .job(mapToJobResponse(updatedApplication.getJob()))
                .candidate(updatedApplication.getCandidate().getName())
                .status(updatedApplication.getStatus())
                .appliedAt(updatedApplication.getAppliedAt().toString())
                .build();

    }

    private JobResponse mapToJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId().toString())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType().name())
                .postedBy(job.getRecruiter().getName())
                .build();
    }
}
