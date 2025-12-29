package com.jerdiys.jobtracker.application;

import com.jerdiys.jobtracker.dtos.ApplicationResponse;
import com.jerdiys.jobtracker.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Application Management", description = "Endpoints for managing job applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(summary = "Get All Applications", description = "Retrieve a list of all job applications in the system. Only accessible by administrators.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of applications")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody List<ApplicationResponse> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @Operation(summary = "Apply to Job", description = "Submit a job application for the specified job. Only accessible by candidates.")
    @ApiResponse(responseCode = "200", description = "Application submitted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Candidate role required")
    @ApiResponse(responseCode = "404", description = "Job not found")
    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public @ResponseBody ApplicationResponse applyToJob(
            @Parameter(description = "ID of the job to apply to", required = true) @PathVariable Long jobId,
            Authentication auth) {
        return applicationService.applyToJob(auth, jobId);
    }

    @Operation(summary = "Get My Applications", description = "Retrieve all job applications submitted by the authenticated candidate.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved candidate's applications")
    @ApiResponse(responseCode = "403", description = "Access denied - Candidate role required")
    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public @ResponseBody List<ApplicationResponse> getMyApplications(Authentication auth) {
        return applicationService.getMyApplications(auth);
    }

    @Operation(summary = "Get Applications for Job", description = "Retrieve all applications submitted for a specific job. Only accessible by recruiters and administrators.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved applications for the job")
    @ApiResponse(responseCode = "403", description = "Access denied - Recruiter or Admin role required")
    @ApiResponse(responseCode = "404", description = "Job not found")
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public @ResponseBody List<ApplicationResponse> getApplicationsForJob(
            @Parameter(description = "ID of the job", required = true) @PathVariable Long jobId,
            Authentication auth) {
        return applicationService.getApplicationsForJob(jobId, auth);
    }

    @Operation(summary = "Update Application Status", description = "Update the status of a job application. Only accessible by recruiters and administrators.")
    @ApiResponse(responseCode = "200", description = "Application status updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Recruiter or Admin role required")
    @ApiResponse(responseCode = "404", description = "Application not found")
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public @ResponseBody ApplicationResponse updateApplicationStatus(
            @Parameter(description = "ID of the application", required = true) @PathVariable Long applicationId,
            @Parameter(description = "New status for the application", required = true) @RequestParam ApplicationStatus status,
            Authentication auth) {
        return applicationService.updateApplicationStatus(applicationId, status, auth);
    }
}
